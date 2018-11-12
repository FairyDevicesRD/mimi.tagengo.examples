package jp.fairydevices.mimi.example;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

class Recorder {
    private final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    private final int SAMPLINGRATE = 16000;
    private final int BUFFER_SIZE_IN_BYTE = 8192;

    private AudioRecord record = null;
    private ExecutorService executor = null;
    private Future<?> future = null;
    private RecorderTask task = null;

    Thread th = null;

    private LinkedBlockingQueue<byte[]> mRecordingQueue;

    Recorder(LinkedBlockingQueue<byte[]> recordingQueue) {
        mRecordingQueue = recordingQueue;
        init();
    }

    void startRecording() {
        executor = Executors.newSingleThreadExecutor();
        task = new RecorderTask();
        future = executor.submit(task);
    }

    void stopRecording() {
        task.stop();
        try {
            future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            record = new AudioRecord.Builder()
                    .setAudioSource(AUDIO_SOURCE)
                    .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTE)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AUDIO_ENCODING)
                            .setSampleRate(SAMPLINGRATE)
                            .setChannelMask(CHANNEL_MASK)
                            .build()).build();
        } else {
            new AudioRecord(AUDIO_SOURCE, SAMPLINGRATE, CHANNEL_MASK, AUDIO_ENCODING, BUFFER_SIZE_IN_BYTE);
        }
    }

    private class RecorderTask implements Runnable {
        private final int RECORDING_BLOCK = 1024;
        volatile boolean  done = false;

        private int readBlock() {
            int readBytes;
            byte[] buf = new byte[RECORDING_BLOCK];
            readBytes = record.read(buf, 0, buf.length);
            //Log.d(getClass().getName(), "readBytes=" + readBytes);
            mRecordingQueue.add(buf);
            return readBytes;
        }

        private void stop() {
            done = true;
        }

        @Override
        public void run() {
            Log.d(getClass().getName(), "RecordTask thread is running");
            record.startRecording();
            while (!done) {
                readBlock();
            }
            record.stop();
            Log.d(getClass().getName(), "RecordTask thread is shutdown");

        }
    }

}
