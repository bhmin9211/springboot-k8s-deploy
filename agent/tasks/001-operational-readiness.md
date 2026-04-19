# 001. Operational Readiness

## Goal
Overview를 운영 판단 화면으로 만든다.

## Reference Docs

- `../architecture/PROJECT_EXPANSION_IDEAS.md`
- `../architecture/upgrade_plan.md`

## Scope
- unhealthy pod
- restartCount 상위 pod
- replica drift
- node readiness
- Top Issues 카드
- namespace 이상 상태 요약

## Done Criteria
- API 응답 가능
- Home 화면 카드 표시
- 기존 Overview보다 운영 판단 정보가 분명해짐

## Notes

- 단순 리소스 수치보다 `문제가 있는 상태`를 우선 보여준다.
- 기존 `Home.vue`를 확장하는 방향이 1차로 적절하다.

## Progress
- [ ] planned
