package io.basquiat.grpc.service;

import io.basquiat.grpc.MusicRequest;
import io.basquiat.grpc.MusicResponse;
import io.basquiat.grpc.MusicServiceGrpc.MusicServiceImplBase;
import io.grpc.stub.StreamObserver;

/**
 * 
 * MusicService
 * 
 * created by basquiat
 *
 */
public class MusicServiceImpl extends MusicServiceImplBase {

	/**
	 * stub service method
	 */
	@Override
	public void music(MusicRequest request, StreamObserver<MusicResponse> responseObserver) {
		System.out.println("ok! I get MusicRequest : " + request);
		// 요청을 받고 응답 정보를 생성한다.
		// 비지니스상이라면 실제 받은 요청 정보를 통해서 do something하고 (e.g. db crud등등등)
		// 응답을 주는 방식이라고 생각하면 될듯...
		MusicResponse musicResponse = MusicResponse.newBuilder()
												   .setMusician("John Coltrane")
												   .setAlbum("Lush Life")
												   .build();
		
		// 스트림옵저버에 응답 정보가 있다는 것을 onNext에 담아서 실행하고 완료
		responseObserver.onNext(musicResponse);
		responseObserver.onCompleted();
	}
	
}
