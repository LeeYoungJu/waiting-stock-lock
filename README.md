# catch-waiting-apps
CATCHTABLE WAITING 프로젝트

## 호스트
### 로컬
- Gateway: http://local-gw.catchtablewaiting.com:8087
- API: http://local-api.catchtablewaiting.com:8088

### 알파 
- Gateway: https://alpha-gw.dev.catchtablewaiting.com
- API: https://alpha-api.dev.catchtablewaiting.com

### 리얼
- Gateway: https://gw.catchtablewaiting.com
- API: https://api.catchtablewaiting.com



## waiting-api

1. docker/README.md 참조하여 docker를 실행시켜야 한다.
2. CatchWaitingApiApplication 실행
3. http://localhost:8088/actuator/health 접속


### 로컬 실행 방법

1. AWS SECRET 환경 변수를 지정하는 방법
    - AWS Secret Manager 를 통해서 Secret Key를 가져오므로, pos-api 을 실행할 때는 환경 변수로 AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY 를 지정해주어야 한다.
        - 예) -DAWS_ACCESS_KEY_ID=<본인의 ACCESS_KEY_ID>, -DAWS_SECRET_ACCESS_KEY=<본인의 AWS_SECRET_ACCESS_KEY>
    - 모든 프로세스가 접근할 수 있는 전역 환경 변수에 본인의 AWS ACCESS KEY를 저장하는 것은 권하지 않는다.
2. ~/.aws/credentials 파일을 사용하는 방법
    - spring.profiles.active 값이 null(혹은 default) 일 때, ~/.aws/credentials 파일의 내용을 읽어들인다.
    - 이 때 읽어들이는 profile 값은 AWS_PROFILE 환경 변수로 지정할 수 있다.
        - 예) -DAWS_PROFILE=<본인의 profile>
    - 만약 AWS_PROFILE을 지정하지 않으면 "wad-dev"를 사용한다.
    - AwsSecretAutoSetter 클래스 참고. 



## waiting-data
1. 메소드 네이밍 규칙
   - prefix
     - get~ : 0개 이상 결과가 나와야하는 경우
     - find~ : 1개 이상 결과가 나와야하는 경우 (0개면 throw를 던지기) 

2. 커밋 규칙
   - 형태
     - [#{지라티켓번호}] #{타입}: #{커밋 내용}
   - 타입 종류
     - feat : 새로운 기능 추가
     - fix : 버그 수정
     - docs : 문서 관련
     - style : 스타일 변경 (포매팅 수정, 들여쓰기 추가, …)
     - refactor : 코드 리팩토링
     - test : 테스트 관련 코드
     - build : 빌드 관련 파일 수정
     - ci : CI 설정 파일 수정
     - perf : 성능 개선
     - chore : 그 외 자잘한 수정

3. 머지 규칙
   - 머지는 PR(Pull Request)을 통해서 할 수 있다.
   - 머지는 1개의 Approve를 받아야 진행할 수 있다.
   - 항상 최신 develop 브랜치에서 rebase를 진행하고 변경사항을 체크해 진행한다.
     - Approve를 받더라도 리뷰가 필요하다면 다시 요청한다.
   - 머지는 PR 생성자나, Assignee가 진행한다.