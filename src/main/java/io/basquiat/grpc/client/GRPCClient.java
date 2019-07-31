package io.basquiat.grpc.client;

import io.basquiat.grpc.MusicRequest;
import io.basquiat.grpc.MusicResponse;
import io.basquiat.grpc.MusicServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * grpc test client
 * created by basquiat
 *
 */
public class GRPCClient {
    
	public static void main(String[] args) throws InterruptedException {
		// grpc server로 채널을 생성한다.
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
													  .usePlaintext()
													  .build();
		
		// 요청할 Stub을 생성한다.
		MusicServiceGrpc.MusicServiceBlockingStub stub = MusicServiceGrpc.newBlockingStub(channel);
		
		// 요청 정보 생성
		MusicRequest musicRequest = MusicRequest.newBuilder()
												.setGenre("Jazz")
												.setMusician("John Coltrane")
												.build();
		
		// Stub를 통해서 내가 호출할 서비스를 가져와 요청 정보를 넘긴 이후 응답을 받는다.
		MusicResponse musicResponse = stub.music(musicRequest);
		
		// 콘솔에 응답 정보 찍고 채널을 닫는다. 
		System.out.println("received response : " + musicResponse);
		channel.shutdown();
	}

}
