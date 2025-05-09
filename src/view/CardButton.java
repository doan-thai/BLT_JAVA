package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.imageio.ImageIO;
import model.Card;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CardButton extends JButton {
    private final Card card;
    private ImageIcon cardImage;
    private static ImageIcon backImage;
    
    // Màu sắc cho thẻ
    private static final Color CARD_BACK_COLOR = new Color(94, 129, 172);
    private static final Color CARD_HOVER_COLOR = new Color(129, 161, 193);
    private static final Color CARD_FRONT_COLOR = new Color(236, 239, 244);
    private static final Color CARD_MATCHED_COLOR = new Color(163, 190, 140);
    private static final Border NORMAL_BORDER = BorderFactory.createLineBorder(new Color(46, 52, 64), 2);
    private static final Border REVEALED_BORDER = BorderFactory.createLineBorder(new Color(180, 142, 173), 2);
    private static final Border MATCHED_BORDER = BorderFactory.createLineBorder(new Color(143, 188, 187), 3);

    // Khởi tạo backImage
    static {
        try {
            File backFile = new File("image/card_back.png");
            if (backFile.exists()) {
                BufferedImage backBufferedImage = ImageIO.read(backFile);
                backImage = new ImageIcon(backBufferedImage);
                System.out.println("Đã tải hình ảnh mặt sau thành công");
            } else {
                System.err.println("Không tìm thấy file hình ảnh mặt sau: " + backFile.getAbsolutePath());
                backImage = null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hình ảnh mặt sau: " + e.getMessage());
            e.printStackTrace();
            backImage = null;
        }
    }

    public CardButton(Card card) {
        this.card = card;
        
        setBorder(NORMAL_BORDER);
        setFocusPainted(false);
        setContentAreaFilled(true);
        
        // Tải hình ảnh nhưng không resize ngay
        try {
            File imageFile = new File(card.getImagePath());
            System.out.println("Đang tìm file tại: " + imageFile.getAbsolutePath());
            
            if (imageFile.exists()) {
                BufferedImage originalImage = ImageIO.read(imageFile);
                cardImage = new ImageIcon(originalImage);
                System.out.println("Đã tải hình ảnh thành công: " + card.getImagePath());
            } else {
                System.err.println("Không tìm thấy file: " + imageFile.getAbsolutePath());
                cardImage = null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hình ảnh [" + card.getImagePath() + "]: " + e.getMessage());
            e.printStackTrace();
            cardImage = null;
        }
        
        // Hiệu ứng hover
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isEnabled() && !card.isRevealed() && !card.isMatched()) {
                    setBackground(CARD_HOVER_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isEnabled() && !card.isRevealed() && !card.isMatched()) {
                    setBackground(CARD_BACK_COLOR);
                }
            }
        });
        
        updateAppearance();
    }
    
    private Image getScaledImage(Image srcImg, int width, int height) {
        // Kiểm tra loại hình ảnh gốc để chọn kiểu BufferedImage phù hợp
        BufferedImage originalImage = null;
        if (srcImg instanceof BufferedImage) {
            originalImage = (BufferedImage) srcImg;
        } else {
            // Nếu không phải BufferedImage, tạo một bản sao
            originalImage = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = originalImage.createGraphics();
            g.drawImage(srcImg, 0, 0, null);
            g.dispose();
        }
        
        int imageType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        
        BufferedImage resizedImg = new BufferedImage(width, height, imageType);
        Graphics2D g2 = resizedImg.createGraphics();
        
        // Thiết lập chất lượng cao cho việc render
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        
        // Tính toán kích thước để giữ tỷ lệ khung hình
        int imageWidth = srcImg.getWidth(null);
        int imageHeight = srcImg.getHeight(null);
        
        double aspectRatio = (double) imageWidth / imageHeight;
        int newWidth, newHeight;
        int x = 0, y = 0;
        
        if (width / aspectRatio <= height) {
            newWidth = width;
            newHeight = (int) (width / aspectRatio);
            y = (height - newHeight) / 2;
        } else {
            newHeight = height;
            newWidth = (int) (height * aspectRatio);
            x = (width - newWidth) / 2;
        }
        
        // Vẽ hình ảnh với kích thước mới và canh giữa
        g2.drawImage(srcImg, x, y, newWidth, newHeight, null);
        g2.dispose();
        
        return resizedImg;
    }
    
    public void updateAppearance() {
     int buttonWidth = getWidth();
    int buttonHeight = getHeight();
    
    // Nếu button chưa có kích thước (khi khởi tạo), sử dụng kích thước mặc định
    if (buttonWidth <= 0 || buttonHeight <= 0) {
        buttonWidth = 100;
        buttonHeight = 100;
    }
    
    // Resize cardImage nếu cần
    ImageIcon resizedCardImage = null;
    if (cardImage != null) {
        resizedCardImage = new ImageIcon(
            getScaledImage(cardImage.getImage(), buttonWidth - 4, buttonHeight - 4)
        );
    }
    
    // Resize backImage nếu cần
    ImageIcon resizedBackImage = null;
    if (backImage != null) {
        resizedBackImage = new ImageIcon(
            getScaledImage(backImage.getImage(), buttonWidth - 4, buttonHeight - 4)
        );
    }

    if (card.isMatched()) {
        if (resizedCardImage != null) {
            setIcon(resizedCardImage);
            setText("");
            } else {
                setIcon(null);
                setText(card.getSymbol());
                setFont(new Font("Arial", Font.BOLD, 24));
                setForeground(Color.BLACK);
            }
            setBackground(CARD_MATCHED_COLOR);
            setBorder(MATCHED_BORDER);
            setEnabled(false);
        } else if (card.isRevealed()) {
        	 if (resizedCardImage != null) {
                 setIcon(resizedCardImage);
                 setText("");
            } else {
                setIcon(null);
                setText(card.getSymbol());
                setFont(new Font("Arial", Font.BOLD, 24));
                setForeground(Color.BLACK);
            }
            setBackground(CARD_FRONT_COLOR);
            setBorder(REVEALED_BORDER);
            setEnabled(false);
        } else {
        	 if (resizedBackImage != null) {
                 setIcon(resizedBackImage);
                 setText("");
            } else {
                setIcon(null);
                setText("?");
                setFont(new Font("Arial", Font.BOLD, 24));
                setForeground(Color.WHITE);
            }
            setEnabled(true);
            setBackground(CARD_BACK_COLOR);
            setBorder(NORMAL_BORDER);
        }

        setBorderPainted(true);
        setIconTextGap(0);
        setMargin(new Insets(0, 0, 0, 0));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
    }

    public Card getCard() {
        return card;
    }
}
