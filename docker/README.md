 # Docker Guide

Local 환경 지원을 위한 다음 솔루션들을 구축한다.

- MySQL DB Server (AWS Aurora 대응)

## 컨테이너 설치 

로컬에서 Docker 실행 후, 다음과 같은 명령어를 실행한다. 
```
cd docker
docker-compose -p waiting up
```
> Docker는 최신 버전을 권장한다.   
> 본 환경을 위해서 1.6GB+ 의 도커 메모리가 필요하다. Docker 메모리가 부족한 경우 구동시 일부 인스턴스가 137 코드로 비정상 종료될 수 있다.      
> Apple Silicon 환경에서는 ARM 용으로 빌드된 이미지를 사용하는 것이 좋다. docker-compose.yml 파일 내의 주석 참고.   

## MySQL DB Client 접속하기

```
mysql -uroot -hlocalhost -P43306 -p
```

> mysql root 패스워드 및 포트는 docker/waiting-db/init.sql 을 참조한다.

## MySQL DB 컨테이너에 접속하기

```
docker exec -it waiting-database bash
```

## Redis Client 접속하기

```
redis-cli -h localhost -p 46379
```

## Redis 컨테이너에 접속하기

```
docker exec -it waiting-redis bash
```
