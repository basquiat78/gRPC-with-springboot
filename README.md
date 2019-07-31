# gRPC Basic

## Prerequisites

### Configuration
- Java 1.8.x
- IDE: Spring Tool Suite version 3.9.7


일단 가이드상의 방식대로 하다보면 많은 부분에서 걸리는게 많다.    

무엇보다 pom.xml에 다음과 같이 맞춰주지 않으면 proto를 자바로 제너레이트 할때 오류가 난다.

```
	<dependency>
		<groupId>com.google.protobuf</groupId>
		<artifactId>protobuf-java</artifactId>
		<version>3.9.0</version>
	</dependency>
```

다음과 같이 추가를 해준다.

참고로 모든 설명은 이클립스 STS를 기준으로 설명하고자 한다.    


일단 메이븐 프로젝트를 생성하면 src/main/proto 폴더는 존재하지 않는다.    
따라서 프로젝트에서 다음 이미지처럼 해서 폴더를 생성한다.


![실행이미지](https://github.com/basquiat78/gRPC-with-springboot/blob/grpc-basic/capture/shot1.png)    


![실행이미지](https://github.com/basquiat78/gRPC-with-springboot/blob/grpc-basic/capture/shot2.png)    


## proto write    

master branch의 README.md에서도 설명했듯이 이것은 일종의 서비스 명세서라고 보며 된다.    
예제로 사용한 MusicService라는 proto파일로 설명을 해본다.

```
syntax = "proto3";
option java_multiple_files = true;
package grpc;

option java_package = "io.basquiat.grpc";

message MusicRequest {
    string genre = 1;
    string musician = 2;
}

message MusicResponse {
    string musician = 1;
    string album = 2;
}

service MusicService {
    rpc music(MusicRequest) returns (MusicResponse);
}

```

syntax는 이것이 proto3라는 것을 명시한다.   

option의 경우에는 보통 다른 쪽에서 사용하는 것을 보면 message만 정의하는 경우가 많은데 그런 경우에는 옵션으로 out_file name을 하나로 만든다.    
아마도 클래스 내부의 inner class로 객체들을 정의하는 방법이라고 볼 수 있는데 위의 경우처럼 'option java_multiple_files = true'처럼 옵션을 주게 되 message, 또는 service에 정의한 파일명으로 객체를 생성하게 된다.

package는 'src/main/java'밑에 생성한 패키지 명을 따라도 된다. 위의 경우에는 따로 줬지만 실제로는 밑에 준 'option java_package = "io.basquiat.grpc"'처럼 package를 'io.basquiat.grpc'로 설정해도 무방하다.    


## Generated-source 생성    

STS에서는 다음 이미지처럼 maven install을 통해서 가능하다.    

![실행이미지](https://github.com/basquiat78/gRPC-with-springboot/blob/grpc-basic/capture/shot3.png)    

성공적으로 maven install이 완료되면 다음 이미지처럼 생성된 코드를 볼 수 있다.    

![실행이미지](https://github.com/basquiat78/gRPC-with-springboot/blob/grpc-basic/capture/shot4.png)    


## Service 작성    

프로젝트의 MusicServiceImpl.java를 살펴보자.    

```
package io.basquiat.grpc.service;

import io.basquiat.grpc.MusicRequest;
import io.basquiat.grpc.MusicResponse;
import io.basquiat.grpc.MusicServiceGrpc.MusicServiceImplBase;
import io.grpc.stub.StreamObserver;

public class MusicServiceImpl extends MusicServiceImplBase {

	@Override
	public void music(MusicRequest request, StreamObserver<MusicResponse> responseObserver) {
		System.out.println("ok! I get MusicRequest : " + request);
	
		MusicResponse musicResponse = MusicResponse.newBuilder()
												   .setMusician("John Coltrane")
												   .setAlbum("Lush Life")
												   .build();
		
		responseObserver.onNext(musicResponse);
		responseObserver.onCompleted();
	}
	
}

```
제너레이트 된 소스 중 MusicServiceGrpc클래스 내부의 MusicServiceImplBase를 extends해서 코드를 작성하면 해당 비지니스 로직은 끝난다.    

간단하게 어떤 요청이 들어오면 해당 요청을 콘솔에 찍고 MusicResponse객체에 정보를 담아내면 끝난다.     



## Call Stub    

GRPCClient.java를 참조하자.    


```
package io.basquiat.grpc.client;

import io.basquiat.grpc.MusicRequest;
import io.basquiat.grpc.MusicResponse;
import io.basquiat.grpc.MusicServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCClient {
    
	public static void main(String[] args) throws InterruptedException {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
													  .usePlaintext()
													  .build();
		MusicServiceGrpc.MusicServiceBlockingStub stub = MusicServiceGrpc.newBlockingStub(channel);
		MusicRequest musicRequest = MusicRequest.newBuilder()
												.setGenre("Jazz")
												.setMusician("John Coltrane")
												.build();
		MusicResponse musicResponse = stub.music(musicRequest);
		System.out.println("received response : " + musicResponse);
		channel.shutdown();
	}

}

```

코드 흐름은 채널을 생성해서 그 채널을 통해 Stub를 주고 받는 방식으로 되어 있다.    
Stub을 통해서 요청 정보를 넘기고 MusicService.proto에서 설정했던 서비스 music을 통해서 응답을 받아서 콘솔에 찍는다.    

특이한 것은 message로 정의한 객체들은 빌더패턴이 적용된다. 다만 get이 붙는게 좀 맘에 안들 뿐이다.    
커스터마이징 할 수 있다는데 거기까진 아직....


## At A Glance    

확실히 Stub/Skeleton을 떠올리게 만든다.    
장점이 많다고 하는데 밑에 글을 통해서 확인해 보면 되겠다.


[Microservices with gRPC](https://medium.com/@goinhacker/microservices-with-grpc-d504133d191d)    



[gRPC란 무엇이고 어떻게 구성되나요?](https://widian.github.io/blog/2018/11/23/grdc-%EC%A0%95%EB%A6%AC.html)    


몇 가지 드는 생각은 '이걸 왜 쓰지?'가 먼저였다.    

굳이 쓸 이유가 있을까 생각이 드는데 위에 글들을 보면 결국 MSA에 최적화된 것이 아닌가 싶다.    

자바 진영에서 바라볼 때는 좀 불편하다는 생각도 드는데 다만 이런 점은 확실히 강점이다.    

1. 서비스에 필요한 것들을 proto를 통해 정의한다. 필요한 요청/응답 도메인을 작성하고 서비스를 정의한다.    
  - 이거 하나면 다른 언어에서도 gRPC를 쓸 수 있다. 폴리그랏!!
2. 이렇게 정의된 proto를 서버/클라이언트가 공유하면서 로직의 흐름이 깨지지 않는다는 것.    
3. 놀라운 비용 절감!! <--- 와우 보통 3배 정도라고 한다.
  - 예를 들면 데이터로 인한 비용이 드는 구조, 예를 들면 클라우드 환경에서 비용 절감이 무려 3개라고 하니....

그러나 만일 응답/요청 도메인이 변경된다면 어떻게 되나??라는 생각이 먼저 드네?   

그렇다면 단점은 무엇일까?
1. 아직은 브라우저와는...
2. REST는 흔히 쓰는 Json포멧이라 human-readable, 그러나 gRPC는..응 아니야~~ 


일단 이렇게 보면 MSA구조에서 백엔드끼리 통신에서는 최적화라는 생각이 든다.     

## On Next    
일단 지금 예제는 UnaryEvent라 해서 1:1로 메세지를 주고 받는 방식이다.    

간단하다는 의미이다. 근데 gRPC는 1:N, 양방향 통신이 가능하다.    
즉, newBlockingStub, newStub, newFutureStub를 제공한다.    
관련 통신방식에 대해서 적용해 볼 예정이다.

--> newStub, newBlockingStub	 테스트 예제 추가 완료   
    newFutureStub의 경우에는 동기랑 좀 비슷하네? 라는 생각이 들어서 일단은 패쓰...
    다음은 양방향 통신 관련 로직을 구현해 볼 예정이다. 
