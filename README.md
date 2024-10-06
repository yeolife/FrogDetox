### 🔗Link
[청깨구리 - 슬립모드&투두리스트 - Google Play 앱](https://play.google.com/store/apps/details?id=com.ssafy.frogdetox&hl=ko)

## 📢 개요
"개구리를 생각하지 마세요." 라고 하면 무엇이 생각나시나요? 당신의 이러한 심리를 이용한 청개구리 투두리스트 기능입니다! 생성형 AI로 최근 작성한 투두를 기반으로 할일을 추천받고, 잠 잘 시간에 게이미피케이션한 앱 제한으로 불쾌하지 않게 도파민 디톡스를 할 수 있습니다. 현대인은 유튜브나 인스타그램과 같이 중독성 있는 플랫폼으로 일정을 지키지 못해서 다음 날 숙면을 충분히 취하지 못하는 일이 빈번하게 발생합니다. 이로 인해 능률이 떨어지고, 불규칙적인 수면 패턴으로 건강이 나빠집니다. 청깨구리로 수면 패턴을 지켜보세요!
<br><br>

</aside>

<div align="center">  
  <img src="https://github.com/yeolife/frogDetox/assets/82012857/42d157ad-236e-47af-adf9-d363de4fff63" width="25%" height="35%">
  <img src="https://github.com/yeolife/frogDetox/assets/82012857/9a29470a-ab7f-4392-a855-d2c67ebad209" width="25%" height="35%">
</div>
<br>
<img src="https://github.com/yeolife/frogDetox/assets/82012857/343cb9c2-ee4d-430f-9633-abc28ffd5412">
<br><br>
<img src="https://github.com/yeolife/frogDetox/assets/82012857/fda22bcd-ffe2-4b2e-9037-8aee89218d8a">
<br><br><br>

## 📢 전체 기능

- 구글 로그인
    - 작성한 투두 목록을 다른 기기에서 동기화
- 투두 리스트 작성
    - RealTime Firebase로 날짜 별 투두를 CRUD
    - OpenAI API를 활용한 생성형 AI의 투두 추천
    - Alarm Manager로 설정한 시간에 알림
- 슬립 모드
    - 설정한 시간에 스마트폰을 하면 화면을 가리는 청개구리 미니게임 생성
- 앱 제한 모드
    - 제한 설정한 앱을 실행하면 화면을 잠그는 청개구리 화면이 덮음
 
<br>

## 🛠️ 사용 기술 및 라이브러리

- Android, Kotlin
- Firebase Auth
    - 구글 로그인
    - 기기 간 데이터 동기화
- Realtime Firebase
    - 투두 데이터 저장
- AlarmManager
    - 투두 예약 알림
    - 슬립모드 예약
- Room DB
    - AlarmManager은 재부팅 시 초기화되기 때문에 알람을 재등록을 위해 사용
- ViewPager2
    - 스와이프로 Fragment 화면전환
- swipedecorator
    - RecyclerView item인 투두를 스와이프로 간편 삭제
- kizitonwose Calender
    - 투두 일정 캘린더를 커스텀
- OpenAI API
    - 프롬프팅을 기반으로 투두 추천 기능 제공

<br>

## 💡 깨달은 점

- Alarm Manager은 안드로이드 시스템에서 관리됨
    - 앱이 꺼져도 알림이 오는 이유가 이때문
- FCM이 아닌, Alarm Manager를 사용한 이유
    - 네트워크 없이 특정 시간에 예약된 알림을 받을 수 있음.
    - 사용자의 개별적인 투두 알람이기 때문에 로컬에서 알림이 관리되는 것이 적절함
- 안드로이드는 푸시알림이 쌓이지 않음
- 접근성(AccessibilityService) 권한에 안드로이드 자체 버그가 있음
    - 배터리 성능 때문인지 자동 해제됨
- RealtimeFirebase는 네트워크가 연결되어 있지 않은 상태에서 CRUD를 해도 네트워크가 연결되었을 때, 자동으로 동기화 됨
