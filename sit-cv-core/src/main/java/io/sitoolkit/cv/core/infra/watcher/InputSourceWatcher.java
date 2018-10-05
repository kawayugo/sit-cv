/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sitoolkit.cv.core.infra.watcher;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 入力ソースの変更を監視するクラスです。
 *
 * @author yuichi.kuwahara
 */
@Slf4j
public abstract class InputSourceWatcher {

    private boolean isContinue = false;

    Set<String> waitingSources = new HashSet<>();
    Instant lastSourceChangedTime;

    final long RELOAD_WAIT_TIME_MILLIS = 300;

    /**
     * 入力ソースを監視対象に追加します。 実際の処理はサブクラスに委譲します。 また、プロセス開始後の初回実行時には監視中ファイルを作成します。
     * このメソッドは繰り返し生成モードでない場合、何も行いません。
     *
     * @param inputSource
     *            入力ソース
     * @see #watchInputSource(java.lang.String)
     */
    public void watch(String inputSource) {
        if (!isContinue()) {
            return;
        }

        watchInputSource(inputSource);
    }

    /**
     * 入力ソースの監視を開始します。 実際の処理はサブクラスに委譲します。 このメソッドは繰り返し生成モードでない場合、何も行いません。
     *
     * @param cg
     *            繰り返し生成インターフェース
     * @see #watching()
     */
    public void start(final ContinuousGeneratable cg) {
        if (!isContinue()) {
            return;
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(() -> {
            while (isContinue()) {
                Set<String> inputSources = watching();
                putInputSources(inputSources);
                log.info("Detected input source change {}", inputSources);
            }
        });

        executor.execute(() -> {
            while (isContinue()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RELOAD_WAIT_TIME_MILLIS);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                getReadyToRegenerateSources().ifPresent(sources -> {
                    log.info("Regenarate from source change {}", sources);
                    cg.regenerate(sources);
                });
            }
        });
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    private synchronized void putInputSources(Collection<String> inputSources) {
        waitingSources.addAll(inputSources);
        lastSourceChangedTime = Instant.now();
    }

    private synchronized Optional<Set<String>> getReadyToRegenerateSources() {
        if (!waitingSources.isEmpty() &&
                Instant.now().isAfter(lastSourceChangedTime.plusMillis(RELOAD_WAIT_TIME_MILLIS))) {
            Set<String> result = new HashSet<>(waitingSources);
            waitingSources.clear();
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    /**
     * 入力ソースを監視対象に追加する実際の処理を実装します。
     *
     * @param inputSource
     *            入力ソース
     */
    protected abstract void watchInputSource(String inputSource);

    /**
     * 入力ソースの監視を開始します。 サブクラスの実装責務は以下の通りです。
     * <ul>
     * <li>入力ソースの変更の監視
     * <li>変更を検知した入力ソースで繰り返しインターフェースの再生成メソッドを実行
     * </ul>
     *
     */
    protected abstract Set<String> watching();

    protected abstract void end(ContinuousGeneratable cg);
}
