/*
 * @file BlockingQueue.h
 * \~english
 * @brief Simple blocking queue implementation which has pop with time out, thread safe
 * \~japanese
 * @brief 簡易的なブロッキングキューの実装、スレッドセーフ
 * \~
 * @copyright Copyright 2018 Fairy Devices Inc. http://www.fairydevices.jp/
 * @copyright Apache License, Version 2.0
 * @author Masato Fujino, created on: 2018/02/16
 *
 * Copyright 2018 Fairy Devices Inc. http://www.fairydevices.jp/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef EXAMPLES_INCLUDE_BLOCKINGQUEUE_H_
#define EXAMPLES_INCLUDE_BLOCKINGQUEUE_H_

#include <mutex>
#include <condition_variable>
#include <deque>


template<class T, class Container = std::vector<T> >
class AudioChunk {
public:
    AudioChunk() : final_(false) {
    }

    explicit AudioChunk(bool final) : final_(final) {
    }

    explicit AudioChunk(const Container &data) : data_(data), final_(false) {
    }

    AudioChunk(const Container &data, bool final) : data_(data), final_(final) {
    }

    Container &data() {
        return data_;
    }

    bool final() const {
        return final_;
    }


private:
    Container data_;
    bool final_;
};

template<class T>
class FinalAudioChunk : public AudioChunk<T> {
public:
    FinalAudioChunk()
            : AudioChunk<T>(true) {
    }
};

template<class T, class Container = std::deque<T>>
class BlockingQueue {
public:
    /**
     * @brief Push to queue
     * @param [in] value A value to be pushed.
     */
    void push(T const &value) {
        {
            std::unique_lock<std::mutex> lock(mutex_);
            queue_.push_front(value);
        }
        condition_.notify_one();
    }

    /**
     * @brief Pop from queue with timeout
     * @param [out] value A value popped from the queue
     * @param [in] timeout Timeout for pop
     * @return True if successfully popped, false if timed out.
     */
    bool pop(T &value, std::chrono::milliseconds timeout) {
        std::unique_lock<std::mutex> lock(mutex_);
        if (!condition_.wait_for(lock, timeout, [=] {
            return !queue_.empty();
        })) {
            return false;
        }
        value = queue_.back();
        queue_.pop_back();
        return true;
    }

    void pop(T &value) {
        std::unique_lock<std::mutex> lock(mutex_);
        condition_.wait(lock, [=] {
            return !queue_.empty();
        });
        value = queue_.back();
        queue_.pop_back();
    }

    /**
     * @brief Return size of container
     * @return The size of the container
     */
    int size() const {
        return queue_.size();
    }

private:
    std::mutex mutex_;
    std::condition_variable condition_;
    Container queue_;
};

/**
 * \~english Blocking queue with the short type of element.
 * \~japanese Short 型のサンプルを要素に持つブロッキングキューの型エイリアス
 */
using AudioBlockingQueue = BlockingQueue<AudioChunk<short> >;

#endif /* EXAMPLES_INCLUDE_BLOCKINGQUEUE_H_ */
