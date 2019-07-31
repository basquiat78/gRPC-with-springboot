package io.basquiat.grpc.server;

import java.io.IOException;

import io.basquiat.grpc.service.MusicServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * grpc server open
 * created by basquiat
 *
 */
public class GRPCServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		// grpc 서버를 띄운다.
		// 서버를 띄우면서 프로시저 서비스를 서비스에 추가한다.
		// 아마도 서비스를 여러게 추가할수도 있고 여타 필터 및 기능들을 설정할 수 있는 듯...
		Server server = ServerBuilder.forPort(8080)
									 .addService(new MusicServiceImpl()).build();

		// 서버 스타트
		System.out.println("Starting server...");
		server.start();
		System.out.println("Server started!");
		server.awaitTermination();
	}

}
