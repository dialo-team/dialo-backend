# Messaging backend handoff

Tai lieu nay la ban handoff ngan gon de frontend tich hop messaging theo trang thai backend hien tai.

Tai lieu lien quan:
- `resources/frontend-messaging-ui-behavior-spec.md`
- `resources/conversation-api-contract.md`
- `resources/message-realtime-flow.md`
- `resources/messaging-frontend-integration-status.md`

## 1. Trang thai hien tai

Backend da khoa contract bang service test + controller contract test cho:
- direct chat
- group management
- group chat dung chung message flow
- message actions chinh
- poll management co ban
- call message/lifecycle co ban

Tong test da xanh:
- `71/71`

## 2. Production-ready de frontend dung ngay

### Conversation
- list conversations
- get conversation detail
- create/reuse direct conversation
- create group
- add/remove member
- assign role `ADMIN/MEMBER`
- transfer `OWNER`
- leave group
- dissolve group
- mark read
- typing

### Message
- send text
- send sticker
- send image/file attachment
- send first direct message
- send reply
- reaction
- pin / unpin
- revoke
- forward
- delete for me
- delete for everyone
- edit

### Poll
- create poll
- vote
- add option
- close poll
- delete poll
- get answer voters

### Call
- create call item
- end call item

### Realtime
- inbox topic
- thread topic
- typing topic
- JWT auth cho STOMP
- read receipt tren `MessageResponse.readBy`

## 3. Cac rule frontend can tin

- dedupe theo `message.id`
- reaction/pin/revoke/edit/poll update la upsert, khong append bubble moi
- `delete for me` khong co shared thread event
- `poll.results.selectedAnswerIds` nen uu tien doc tu REST detail/requester-specific response
- `PUT /read` se cap nhat `readBy` qua thread update tren last-read message
- call item chi la message contract, chua bao gom signaling/WebRTC

## 4. Chua co ro rang

- signaling/WebRTC session
