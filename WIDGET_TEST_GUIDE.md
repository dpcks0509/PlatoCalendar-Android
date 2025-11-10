# 🧪 PLATO 캘린더 위젯 테스트 가이드

## ✅ 등록 완료!

위젯이 성공적으로 등록되었습니다. 이제 테스트할 수 있습니다.

## 📋 등록된 내용

### 1. AndroidManifest.xml

```xml
<receiver
    android:name="pusan.university.plato_calendar.presentation.widget.CalendarWidgetReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/calendar_widget_info" />
</receiver>
```

### 2. res/xml/calendar_widget_info.xml

- **크기**: 250dp x 120dp (최소)
- **셀**: 4 x 2
- **업데이트 주기**: 30분 (1800000ms)
- **크기 조절**: 가로/세로 가능

### 3. res/values/strings.xml

- `widget_description`: "PLATO 캘린더 일정을 확인하세요"

## 🚀 테스트 방법

### 1단계: 앱 빌드 및 설치

```bash
# Debug 빌드
./gradlew assembleDebug

# 또는 Android Studio에서
Run -> Run 'app' (Shift + F10)
```

### 2단계: 위젯 추가

#### 📱 방법 1: 홈 화면에서 직접 추가

1. **홈 화면 길게 누르기** (약 1~2초)
2. **"위젯"** 메뉴 선택
3. **"PLATO 캘린더"** 찾기
    - 앱 이름으로 검색하거나 스크롤해서 찾기
4. **위젯 드래그하여 홈 화면에 배치**
5. 크기 조절 (선택사항)

#### 📱 방법 2: 앱 서랍에서 추가 (일부 런처)

1. 앱 서랍 열기
2. 위젯 탭 선택
3. PLATO 캘린더 위젯 찾기
4. 홈 화면으로 드래그

### 3단계: 위젯 기능 테스트

#### ✨ 초기 상태 확인

위젯이 추가되면 다음과 같이 표시됩니다:

```
┌─────────────────────┐
│   PLATO 캘린더      │
│                     │
│   일정 개수: 0      │
│                     │
│   마지막 업데이트:  │
│   아직 업데이트 안됨│
│                     │
│   [🔄 새로고침]     │
└─────────────────────┘
```

#### 🔄 새로고침 테스트

**테스트 케이스 1: 로그아웃 상태**

1. 앱에서 로그아웃 상태 확인
2. 위젯의 **"🔄 새로고침"** 버튼 클릭
3. ✅ **확인사항**:
    - 일정 개수가 학사 일정만 표시되는지
    - 마지막 업데이트 시간이 업데이트되는지
    - 예: `2025-01-11 14:30:45`

**테스트 케이스 2: 로그인 상태**

1. 앱에서 PLATO 로그인
2. 위젯의 **"🔄 새로고침"** 버튼 클릭
3. ✅ **확인사항**:
    - 일정 개수가 증가했는지 (학사 + 개인 일정)
    - 로그아웃 때보다 많은 일정이 표시되는지

**테스트 케이스 3: 연속 클릭**

1. 새로고침 버튼 여러 번 클릭
2. ✅ **확인사항**:
    - 매번 업데이트 시간이 변경되는지
    - 일정 개수가 일관되게 표시되는지

**테스트 케이스 4: 앱에서 일정 추가 후**

1. 앱에서 새로운 일정 추가
2. 위젯으로 돌아가서 새로고침
3. ✅ **확인사항**:
    - 일정 개수가 1 증가했는지

## 🐛 문제 해결

### 문제 1: 위젯이 위젯 목록에 보이지 않음

**해결방법**:

1. 앱 완전히 종료
2. 디바이스 재시작
3. 앱 재설치

```bash
./gradlew uninstallAll
./gradlew installDebug
```

### 문제 2: 새로고침 버튼을 눌러도 반응이 없음

**해결방법**:

1. Logcat 확인:

```bash
adb logcat | grep -i "CalendarWidget\|RefreshSchedules"
```

2. 네트워크 연결 확인
3. 앱 권한 확인 (인터넷 권한)

### 문제 3: 일정 개수가 0으로 표시됨

**가능한 원인**:

- 네트워크 오류
- 서버 응답 없음
- 로그인 세션 만료

**해결방법**:

1. 앱에서 직접 일정이 보이는지 확인
2. 로그인 상태 확인
3. 네트워크 연결 확인

### 문제 4: ColorProvider 경고

**상태**: ✅ 정상 (무시 가능)

```
ColorProviderKt.ColorProvider can only be called from within the same library group
```

- 이는 Glance 라이브러리의 알려진 제한사항
- 실제 앱 동작에는 영향 없음

## 📊 예상 결과

### 성공적인 테스트 시나리오

```
시작 상태:
━━━━━━━━━━━━━━━━━━
PLATO 캘린더
일정 개수: 0
마지막 업데이트: 아직 업데이트 안됨

▼ 로그아웃 상태에서 새로고침

━━━━━━━━━━━━━━━━━━
PLATO 캘린더
일정 개수: 5
마지막 업데이트: 2025-01-11 14:30:45

▼ 로그인 후 새로고침

━━━━━━━━━━━━━━━━━━
PLATO 캘린더
일정 개수: 23
마지막 업데이트: 2025-01-11 14:31:02
```

## 🔍 디버깅 팁

### Logcat 필터 사용

```bash
# CalendarWidget 관련 로그만 보기
adb logcat -s CalendarWidget

# 에러만 보기
adb logcat *:E

# Hilt 관련 로그
adb logcat | grep -i "hilt\|inject"
```

### 위젯 강제 업데이트

```bash
# 위젯 데이터 초기화
adb shell am broadcast -a android.appwidget.action.APPWIDGET_UPDATE

# 앱 데이터 초기화 (주의: 모든 데이터 삭제됨)
adb shell pm clear pusan.university.plato_calendar
```

## ✅ 체크리스트

테스트 완료 시 다음을 확인하세요:

- [ ] 위젯이 위젯 목록에 표시됨
- [ ] 위젯을 홈 화면에 추가할 수 있음
- [ ] 초기 상태가 "일정 개수: 0"으로 표시됨
- [ ] 새로고침 버튼이 작동함
- [ ] 로그아웃 상태에서 학사 일정을 가져옴
- [ ] 로그인 상태에서 더 많은 일정을 가져옴
- [ ] 업데이트 시간이 올바르게 표시됨
- [ ] 여러 번 새로고침해도 정상 작동함
- [ ] 위젯 크기 조절이 가능함

## 🎯 다음 단계

위젯이 정상 작동하면:

1. ✅ UI 개선 (실제 일정 목록 표시)
2. ✅ 자동 업데이트 기능 추가
3. ✅ 다크 테마 지원
4. ✅ 위젯 크기별 레이아웃 최적화

---

**문제가 발생하면 Logcat을 확인하고 오류 메시지를 공유해주세요!** 🚀
