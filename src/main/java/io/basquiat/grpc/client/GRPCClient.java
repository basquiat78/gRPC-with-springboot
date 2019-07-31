package io.basquiat.grpc.client;

import io.basquiat.grpc.MusicRequest;
import io.basquiat.grpc.MusicResponse;
import io.basquiat.grpc.MusicServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * grpc test client
 * created by basquiat
 *
 */
public class GRPCClient {
    
	public static void main(String[] args) throws InterruptedException {
		
		/**
		 * 현재 테스트는 다음과 같은 방식으로 진행한다.
		 * async로 호출한다.
		 * 그이후 바로 sync로 호출한다.
		 * 
		 * 로그를 찍어보면 aysnc이후 sync로 호출하기에 동기 먼저 로그 찍히고 그 다음 async호출후 응답 로그가 찍힌다.
		 * 
		 * 하지만 무조건적이진 않고 비동기 호출후 동기 호출하기 전에 응답을 받는 경우도 있어서 순서는 때에 따라서 다르게 찍힌다.
		 * 
		 */
		
		// grpc server로 채널을 생성한다.
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
													  .usePlaintext()
													  .build();
		
		// 요청 정보 생성
		MusicRequest musicRequest = MusicRequest.newBuilder()
												.setGenre("Jazz")
												.setMusician("Charlie Parker")
												.build();
		
		// 서버로부터 받을 콜백 응답 객체
		StreamObserver<MusicResponse> responseObserver = new StreamObserver<MusicResponse>() {
			@Override
			public void onNext(MusicResponse response) {
				System.out.println("비동기 응답 : " + response);
			}

            @Override
            public void onError(Throwable t) {
            	System.out.println("비동기 Error 응답");
            }

            @Override
            public void onCompleted() {
            	System.out.println("비동기 호출 completed");
            }
        };
		
        // 요청할 Stub을 생성한다. --> async
        MusicServiceGrpc.MusicServiceStub syncStub = MusicServiceGrpc.newStub(channel);
		syncStub.music(musicRequest, responseObserver);
		
		// 요청할 Stub을 생성한다. --> blocking
		MusicServiceGrpc.MusicServiceBlockingStub blockStub = MusicServiceGrpc.newBlockingStub(channel);
		
		// 요청 정보 생성
		MusicRequest musicRequest1 = MusicRequest.newBuilder()
												.setGenre("Jazz")
												.setMusician("John Coltrane")
												.build();
		
		// Stub를 통해서 내가 호출할 서비스를 가져와 요청 정보를 넘긴 이후 응답을 받는다.
		MusicResponse musicResponse = blockStub.music(musicRequest1);
		// 콘솔에 응답 정보 찍고 채널을 닫는다. 
		System.out.println("blocking received response : " + musicResponse);
		channel.shutdown();
		
	}

}
