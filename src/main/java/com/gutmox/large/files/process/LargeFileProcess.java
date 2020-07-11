package com.gutmox.large.files.process;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.FlowableHelper;
import io.vertx.reactivex.core.Vertx;

public class LargeFileProcess {

	Completable doProcess(Vertx vertx){
		OpenOptions oo = new OpenOptions();
		return vertx.fileSystem().rxOpen("small_file.txt", oo).flatMapCompletable(file ->{
			final Flowable<Buffer> bufferFlowable = FlowableHelper.toFlowable(file.getDelegate());
			bufferFlowable.buffer(300).forEach(data -> System.out.println("Read data: " + data));

			return Completable.complete();
		}).doOnError(Throwable::printStackTrace);
	}
}
