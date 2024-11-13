## 📝 이슈 설명

다음과 같이 꾸며보려고 합니다 ㅎㅎ
- 서비스 로직의 이유가 정말 중요하다. ex) 왜 sqs를 사용했는지, 어떻게 확장될지.. 등등 사이드 이펙트도 정리해서 말해주기
- 깃헙 위키

---

# Team13_BE
<div align="center">

<img src="https://github.com/user-attachments/assets/89319294-ed1d-497d-834d-b2c6719367ae" width="30%">

최고의 요양원 관리 서비스, '돌봄다리'의 백엔드 서버입니다.

해당 레포지토리는 [카카오테크캠퍼스](https://www.kakaotechcampus.com/) 2기 부산대 13조 프로젝트에 기반을 두고 있습니다.

</div>

## Collaborators

<h3 align="center">Backend</h3>

<div align="center">

| **테크 리더** | **기획 리더** | **리액셔너** | **리마인더** | **리마인더** |
| ------------- | ------------- | ------------ | ------------ | ------------ |
| <div align="center">[이영준](https://github.com/20jcode)</div> | <div align="center">[김태윤](https://github.com/pykido)</div> | <div align="center">[유경미](https://github.com/yooookm)</div> | <div align="center">[박혜연](https://github.com/hyyyh0x)</div> | <div align="center">[이진솔](https://github.com/mogld)</div> |
| <div align="center"><img src="https://avatars.githubusercontent.com/u/109460399?v=4" width="100"></div> | <div align="center"><img src="https://github.com/user-attachments/assets/b6434e99-2e5d-4d46-92f0-55004d16ec3c" width="100"></div> | <div align="center"><img src="https://github.com/user-attachments/assets/9a2c803f-a49f-4343-8de3-ae8de72b7927" width="100"></div> | <div align="center"><img src="https://avatars.githubusercontent.com/u/141637975?v=4" width="100"></div> | <div align="center"><img src="https://avatars.githubusercontent.com/u/143364802?v=4" width="100"></div> |

</div>

<h3 align="center">Frontend</h3>


<div align="center">

| **조장**      | **타임 키퍼** |
| ------------- | ------------- |
|<div align="center">[문정윤](https://github.com/nnoonjy)</div>|<div align="center">[이지수](https://github.com/dlwltn0430)</div> |
| <div align="center"><img src="https://avatars.githubusercontent.com/u/102630375?v=4" width="100"></div> | <div align="center"><img src="https://avatars.githubusercontent.com/u/101401447?v=4" width="100"></div> |

</div>


## Introduction
'돌봄다리'는 요양원, 요양보호사, 보호자 간의 소통을 원활하게 하기 위한 디지털 차트 작성 서비스입니다. 음성 인식과 손글씨 인식 기능을 통합하여 50-70대의 요양보호사들이 간편하고 효율적으로 일지를 작성할 수 있도록 설계되었습니다. 또한, 보호자들은 가족의 상태를 투명하게 확인할 수 있어 신뢰성 있는 서비스입니다. 추가적으로 자체 알림 서비스와 AI 서비스를 도입하여 일지 요약, 보호자와 요양보호사들과의 연락 등을 자동화하였습니다.



## System Structure
### 전체 구상도



### 백엔드 구상도



## ERD


## Tech Stack

<div align="center">

![java 17](https://img.shields.io/badge/-Java%2017-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![spring boot 3.1.3](https://img.shields.io/badge/Spring%20boot%203.1.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![spring security](https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![mysql 8.0](https://img.shields.io/badge/MySQL%208.0-005C84?style=for-the-badge&logo=mysql&logoColor=white)

![Redis 6.2](https://img.shields.io/badge/Redis%206.2-DC382D?style=for-the-badge&logo=Redis&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![Amazon sqs](https://img.shields.io/badge/Amazon%20sqs-FF9900?style=for-the-badge&logo=amazon&logoColor=white)

![Naver cloud](https://img.shields.io/badge/naver%20cloud-03C75A?style=for-the-badge&logo=naver&logoColor=white)
![openAI](https://img.shields.io/badge/openAI-FF6C37?style=for-the-badge&logo=openai&logoColor=white)
![poi](https://img.shields.io/badge/poi-3F6EB5?style=for-the-badge&logo=apache&logoColor=white)
![line api](https://img.shields.io/badge/line%20api-00C300?style=for-the-badge&logo=line&logoColor=white)
![coolSms](https://img.shields.io/badge/coolSms-FF6C37?style=for-the-badge&logo=coolSms&logoColor=white)

</div>

## 구현 기능 목록
### 알림 서비스
1. Line 알림 서비스
    - Line Messaging API
    - Amazon sqs
    - spring scheduler
2. SMS 알림 서비스
    - coolSms API
    - Amazon sqs
    - spring scheduler

### 파일 입출력
- poi 라이브러리

### AI
- open AI
- 파인 튜닝

### OCR
- S3 / presigned url
- naver clovar

### 인증/인가
- spring security
- redis



