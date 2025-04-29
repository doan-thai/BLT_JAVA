package util;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, SoundConfig> soundConfigs;
    private boolean soundEnabled = true;

    // Cấu hình âm thanh cho từng sự kiện
    private static class SoundConfig {
        int[] frequencies; // Tần số (Hz) cho âm thanh
        int[] durations;   // Thời lượng (ms) cho mỗi tần số

        SoundConfig(int[] frequencies, int[] durations) {
            this.frequencies = frequencies;
            this.durations = durations;
        }
    }

    private SoundManager() {
        soundConfigs = new HashMap<>();
        
        initializeSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void initializeSounds() {
        // Cấu hình âm thanh cho từng sự kiện
        soundConfigs.put("flip", new SoundConfig(
            new int[]{500}, // Tần số 500 Hz
            new int[]{100}  // Thời lượng 100ms
        ));
        soundConfigs.put("match", new SoundConfig(
            new int[]{800, 1000}, // Tần số 800 Hz rồi 1000 Hz
            new int[]{100, 100}   // Mỗi tần số 100ms
        ));
        soundConfigs.put("nomatch", new SoundConfig(
            new int[]{300}, // Tần số 300 Hz
            new int[]{300}  // Thời lượng 300ms
        ));
        soundConfigs.put("win", new SoundConfig(
            new int[]{600, 800, 1000, 1200}, // Giai điệu tăng dần
            new int[]{150, 150, 150, 200}    // Thời lượng cho mỗi nốt
        ));
        soundConfigs.put("start", new SoundConfig(
            new int[]{600}, // Tần số 600 Hz
            new int[]{150}  // Thời lượng 150ms
        ));
        System.out.println("Đã khởi tạo cấu hình âm thanh.");
    }

    public void playSound(String name) {
        if (!soundEnabled) return;

        SoundConfig config = soundConfigs.get(name);
        if (config == null) {
            System.err.println("Không tìm thấy cấu hình âm thanh cho: " + name);
            return;
        }

        // Tạo và phát âm thanh trong một luồng riêng để không chặn UI
        new Thread(() -> {
            try {
                // Thiết lập định dạng âm thanh
                AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(audioFormat);
                line.open(audioFormat);
                line.start();

                // Phát từng nốt trong cấu hình
                for (int i = 0; i < config.frequencies.length; i++) {
                    generateSineWave(line, config.frequencies[i], config.durations[i]);
                }

                // Đợi âm thanh phát xong và đóng
                line.drain();
                line.stop();
                line.close();
            } catch (LineUnavailableException e) {
                System.err.println("Lỗi khi phát âm thanh " + name + ": " + e.getMessage());
            }
        }).start();
    }

    private void generateSineWave(SourceDataLine line, int frequency, int duration) {
        int sampleRate = 44100; // Tần số lấy mẫu (Hz)
        int samples = (int) ((duration * sampleRate) / 1000); // Số mẫu cho thời lượng
        byte[] buffer = new byte[2 * samples]; // 2 byte mỗi mẫu (16-bit)

        for (int i = 0; i < samples; i++) {
            // Tạo sóng sine
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            short sample = (short) (Math.sin(angle) * 32760); // Biên độ tối đa cho 16-bit

            // Ghi mẫu vào buffer (little-endian)
            buffer[2 * i] = (byte) (sample & 0xFF);
            buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        // Viết buffer vào line để phát
        line.write(buffer, 0, buffer.length);
    }

    public void toggleSound() {
        soundEnabled = !soundEnabled;
        System.out.println("Âm thanh: " + (soundEnabled ? "Bật" : "Tắt"));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}