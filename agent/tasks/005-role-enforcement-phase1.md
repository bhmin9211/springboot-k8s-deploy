# 005. Role Enforcement Phase 1

## Goal
`VIEWER / OPERATOR / ADMIN` 역할을 UI와 API에 1차 적용한다.

## Reference Docs

- `../architecture/ROLE_MODEL_IDEAS.md`
- `../architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md`
- `../architecture/upgrade_plan.md`

## Scope
- role badge
- 버튼 노출 차등
- API role 검사
- 역할별 route access 초안
- Keycloak role -> application role 매핑

## Done Criteria
- 역할별 화면 차이 확인
- 명령형 API가 역할별로 다르게 동작
- 프론트 숨김과 백엔드 검사 둘 다 적용

## Notes

- 최소 3단계 역할:
  - `VIEWER`
  - `OPERATOR`
  - `ADMIN`
- 공개 포트폴리오 환경은 기본적으로 `VIEWER` 기준으로 설계한다.

## Progress
- [ ] planned
