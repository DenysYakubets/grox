/*
 * Copyright (c) 2017, Groupon, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.groupon.grox;

import static com.groupon.grox.RxStores.states;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import rx.Subscription;
import rx.observers.TestSubscriber;

public class RxStoresTest {

  @Test
  public void states_should_observeInitialState() {
    //GIVEN
    Store<Integer> store = new Store<>(0);
    TestSubscriber testSubscriber = new TestSubscriber();

    //WHEN
    states(store).subscribe(testSubscriber);

    //THEN
    testSubscriber.assertNoTerminalEvent();
    testSubscriber.assertValue(0);
  }

  @Test
  public void states_should_observeStateChanges() {
    //GIVEN
    Store<Integer> store = new Store<>(0);
    TestSubscriber testSubscriber = new TestSubscriber();
    states(store).subscribe(testSubscriber);

    //WHEN
    store.dispatch(integer -> integer + 1);

    //THEN
    testSubscriber.assertNoTerminalEvent();
    testSubscriber.assertValues(0, 1);
  }

  @Test
  public void states_should_stopObservingStateChanges() {
    //GIVEN
    Store<Integer> store = new Store<>(0);
    TestSubscriber testSubscriber = new TestSubscriber();
    final Subscription subscription = states(store).subscribe(testSubscriber);

    //WHEN
    subscription.unsubscribe();
    store.dispatch(integer -> integer + 1);
    final Integer state = store.getState();

    //THEN
    testSubscriber.assertNoTerminalEvent();
    testSubscriber.assertValue(0);
    testSubscriber.assertUnsubscribed();
    assertThat(state, is(1));
  }

  @Test
  public void states_should_unsubscribeListener() {
    //GIVEN
    Store<Integer> mockStore = createMock(Store.class);
    mockStore.subscribe(anyObject());
    mockStore.unsubscribe(anyObject());
    replay(mockStore);

    //WHEN
    states(mockStore).subscribe().unsubscribe();

    //THEN
    verify(mockStore);
  }
}
