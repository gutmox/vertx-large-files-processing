package com.gutmox.large.files.process;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.file.AsyncFile;

public class LargeFileProcess {

	Completable doProcess(Vertx vertx) {

		OpenOptions oo = new OpenOptions();
		return vertx.fileSystem().rxOpen("semi-large.txt", oo)
			.doOnError(Throwable::printStackTrace)
			.flatMapPublisher(AsyncFile::toFlowable).doOnError(Throwable::printStackTrace)
			.flatMap(buffer -> Flowable.fromArray(buffer.toString().split("\n")))
			.flatMapCompletable(line -> {
				System.out.println(line);
				return Completable.complete();
			});
	}

	Completable doProcessSmall(Vertx vertx) {
		OpenOptions oo = new OpenOptions();
		return vertx.fileSystem().rxOpen("small_file.txt", oo).doOnError(Throwable::printStackTrace)
			.flatMapCompletable(file -> {
				file.toFlowable().buffer(300).forEach(data -> System.out.println("Read data: " + data));
				return Completable.complete();
			}).doOnError(Throwable::printStackTrace);
	}
}
