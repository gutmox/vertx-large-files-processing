package com.gutmox.large.files.process;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.file.AsyncFile;
import io.vertx.reactivex.core.parsetools.RecordParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class LargeFileProcess {

	private final String PATH = "/Users/pablo.gutierrez/Documents/repositories/my-projects/vertx-large-files-processing/src/main/resources/";

	Completable doProcess(Vertx vertx) {
		return redFilePureRx("packages_fixed_all.csv")
			.andThen(redFileVertxRecordParser(vertx, "subs_fixed_all_0.csv"));
	}

	private Completable redFilePureRx(String file) {
		AtomicInteger ai = new AtomicInteger(0);
		return Flowable.generate(() -> new BufferedReader(new FileReader(PATH + file)),
			(reader, emitter) -> {
				final String line = reader.readLine();
				if (line != null) {
					emitter.onNext(line);
				} else {
					emitter.onComplete();
				}
			},
			BufferedReader::close
		).flatMapCompletable(line -> {
			final int length = line.toString().split(",").length;
			ai.getAndIncrement();
			if (length != 8) {
				System.out.println(length);
				System.out.println(line);
			}
			return Completable.complete();
		}).doOnTerminate(() -> System.out.println(ai.get()));
	}

	private Completable redFileVertxRecordParser(Vertx vertx, String fileName) {
		AtomicInteger ai = new AtomicInteger(0);
		return vertx.fileSystem().rxOpen(fileName, new OpenOptions())
			.doOnError(Throwable::printStackTrace)
			.flatMapCompletable(csvFile -> RecordParser
				.newDelimited("\n", csvFile)
				.toFlowable()
				.flatMapCompletable(line -> {
					ai.getAndIncrement();
					final int length = line.toString().split(",").length;
					if (length != 8) {
						System.out.println(length);
						System.out.println(line);
					}
					return Completable.complete();
				})
				.doFinally(csvFile::close)
			).doOnTerminate(() -> System.out.println(ai.get()));
	}

	private Completable redFileVertx(Vertx vertx, String fileName) {
		return vertx.fileSystem().rxOpen(fileName, new OpenOptions())
			.doOnError(Throwable::printStackTrace)
			.flatMapPublisher(AsyncFile::toFlowable)
			.doOnError(Throwable::printStackTrace)
			.flatMap(buffer -> Flowable.fromArray(buffer.toString(StandardCharsets.UTF_8).split("\\n")))
			.flatMapCompletable(line -> {
				final int length = line.split(",").length;
				if (length != 8) {
					System.out.println(length);
					System.out.println(line);
				}
				return Completable.complete();
			})
			.doFinally(() -> {
				System.out.println("--------------------------------------");
				System.out.println("--------------------------------------");
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
