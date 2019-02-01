/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.github.reactivex.jean_lopes.database;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonArray;
import io.github.jean_lopes.domain.search_history.LastSearch;
import io.github.jean_lopes.database.PageRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.github.jean_lopes.domain.transaction_history.TransactionKind;
import io.github.jean_lopes.domain.transaction_history.LastTransaction;


@io.vertx.lang.rx.RxGen(io.github.jean_lopes.database.DatabaseService.class)
public class DatabaseService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DatabaseService that = (DatabaseService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<DatabaseService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new DatabaseService((io.github.jean_lopes.database.DatabaseService) obj),
    DatabaseService::getDelegate
  );

  private final io.github.jean_lopes.database.DatabaseService delegate;
  
  public DatabaseService(io.github.jean_lopes.database.DatabaseService delegate) {
    this.delegate = delegate;
  }

  public io.github.jean_lopes.database.DatabaseService getDelegate() {
    return delegate;
  }

  public io.github.reactivex.jean_lopes.database.DatabaseService lastSearch(String cpf, Handler<AsyncResult<LastSearch>> resultHandler) { 
    delegate.lastSearch(cpf, resultHandler);
    return this;
  }

  public Single<LastSearch> rxLastSearch(String cpf) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      lastSearch(cpf, handler);
    });
  }

  public io.github.reactivex.jean_lopes.database.DatabaseService lastTransaction(String cpf, TransactionKind kind, Handler<AsyncResult<LastTransaction>> resultHandler) { 
    delegate.lastTransaction(cpf, kind, resultHandler);
    return this;
  }

  public Single<LastTransaction> rxLastTransaction(String cpf, TransactionKind kind) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      lastTransaction(cpf, kind, handler);
    });
  }

  public io.github.reactivex.jean_lopes.database.DatabaseService transactions(String cpf, PageRequest page, Handler<AsyncResult<JsonArray>> resultHandler) { 
    delegate.transactions(cpf, page, resultHandler);
    return this;
  }

  public Single<JsonArray> rxTransactions(String cpf, PageRequest page) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      transactions(cpf, page, handler);
    });
  }


  public static  DatabaseService newInstance(io.github.jean_lopes.database.DatabaseService arg) {
    return arg != null ? new DatabaseService(arg) : null;
  }
}
