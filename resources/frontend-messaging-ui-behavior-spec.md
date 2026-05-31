# Frontend messaging UI behavior spec

Tai lieu nay da duoc cap nhat theo trang thai backend hien tai, de frontend doc va code truc tiep.

Tai lieu lien quan:
- `resources/messaging-frontend-integration-status.md`
- `resources/conversation-api-contract.md`
- `resources/message-realtime-flow.md`

## 1. Global rules

- REST la nguon su that cho ghi du lieu
- STOMP la kenh dong bo nhanh cho inbox/thread/typing
- moi bubble phai dedupe theo `message.id`
- reaction/pin/revoke/edit/poll update la upsert theo `message.id`
- `delete for me` la local remove sau REST success, khong cho shared thread event
- sau reconnect STOMP:
  - refetch inbox
  - neu dang mo thread thi refetch thread do

## 2. Direct conversation

### 2.1. Tao hoac mo direct
- UI co the:
  - goi `POST /api/v1/conversations`
  - hoac gui first message bang `POST /api/v1/messages/direct`
- direct conversation da ton tai thi backend reuse

### 2.2. Load thread
- mo thread -> `GET /api/v1/conversations/{conversationId}`
- load xong -> `PUT /api/v1/conversations/{conversationId}/read`

## 3. Message flow dung chung cho direct va group

### 3.1. Send text / sticker / file / image / reply
- optimistic bubble truoc
- REST success -> replace bang server item
- STOMP thread ve sau -> upsert theo `message.id`

### 3.2. Forward
- mo modal chon dich
- request:
  - `{ "conversationId": "..." }`
  - hoac `{ "targetUserId": "..." }`
- thread nguon khong doi
- thread dich nhan message moi

### 3.3. Revoke
- chi sender thay action
- bubble giu nguyen vi tri va doi thanh placeholder
- text placeholder: `Tin nhan da duoc thu hoi`

### 3.4. Delete for me
- chi xoa khoi UI cua current user
- bubble bien mat ngay sau REST success
- refetch sau nay khong duoc quay lai

### 3.4.1. Delete for everyone
- chi sender thay action
- thanh cong thi bubble cu doi thanh placeholder `Tin nhan da bi xoa`
- semantics nay khac `revoke`
- frontend upsert theo `message.id`, khong append bubble moi

### 3.5. Edit
- chi sender thay action
- save thanh cong -> bubble cu cap nhat in-place
- hien dau hieu `da chinh sua` dua tren `editedTimeStamp`

### 3.6. Reaction / pin
- thay doi bubble hien tai theo `message.id`
- khong append bubble moi

### 3.7. Typing
- chi hien tren thread dang mo
- bo qua event cua chinh minh
- auto clear sau vai giay neu khong co event moi

### 3.8. Read receipt
- bubble co the hien read receipt nho dua tren `readBy`
- khi `PUT /read` xong, frontend co the nhan thread update tren bubble cuoi da duoc doc
- direct thread co the render avatar/doc trang thai cua ben kia
- group thread co the render so luong hoac danh sach nho tuy UI

## 4. Group management

### 4.1. Tao nhom
- group phai co it nhat 3 nguoi tong cong, tinh ca requester
- requester khong can tu them minh vao danh sach participant
- tao xong mo ngay thread group

### 4.2. Them thanh vien
- owner/admin thay action
- user moi thay group trong inbox sau dong bo

### 4.3. Xoa thanh vien
- owner xoa duoc `ADMIN/MEMBER`
- admin chi xoa duoc `MEMBER`

### 4.4. Gan quyen
- owner co the doi `MEMBER <-> ADMIN`

### 4.4.1. Chuyen owner
- owner hien tai co action transfer owner
- chon 1 active member lam owner moi
- owner cu xuong `ADMIN`
- owner moi len `OWNER`

### 4.5. Roi nhom
- member/admin co action leave
- owner phai transfer owner truoc roi moi leave

### 4.6. Giai tan nhom
- owner co action dissolve
- thanh cong xong group bien mat khoi inbox sau refetch

### 4.7. Block member
- chi `OWNER` va `ADMIN` thay action block/unblock
- blocked member van thay group trong sidebar neu ho da tung la member
- khi mo group, blocked member khong thay thread content, khong thay group detail, khong thao tac duoc gi
- UI chi can hien trang thai current user da bi chan

## 5. Group chat

Group chat dung chung message flow nhu direct:
- send text / file / image / sticker / reply
- reaction / pin / revoke / forward / delete for me / edit
- typing
- mark read
- thread STOMP

Frontend khong can tach 2 bo UI logic message cho direct va group, chi can doi shell conversation header/member panel.

## 6. Poll management

### 6.1. Tao poll
- UI goi `POST /api/v1/conversations/{conversationId}/polls`
- poll render nhu bubble message

### 6.2. Vote poll
- UI goi `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes`
- single-select thi chi chon 1 dap an
- multi-select thi gui nhieu `answerIds`

### 6.3. Them lua chon
- chi creator thay action add option

### 6.4. Dong poll
- chi creator thay action close
- poll dong thi disable vote UI

### 6.5. Xoa poll
- chi creator thay action delete
- sau khi xoa, bubble poll doi theo semantics revoke

### 6.6. Xem voter theo dap an
- UI co the mo modal danh sach voter bang:
  - `GET /api/v1/conversations/{conversationId}/polls/{messageId}/answers/{answerId}`

### 6.7. Selected state
- `selectedAnswerIds` nen uu tien doc tu REST detail/requester-specific response
- STOMP thread event uu tien de dong bo count/finalized state

## 6.8. Call item

- UI co the tao call item bang:
  - `POST /api/v1/conversations/{conversationId}/calls`
- UI co the ket thuc call item bang:
  - `POST /api/v1/conversations/{conversationId}/calls/{messageId}/end`
- bubble call render theo:
  - `type`: `AUDIO` hoac `VIDEO`
  - `status`: `ONGOING` hoac `ENDED`
  - danh sach user
  - `startedTime`
  - `endedTime`

Luu y:
- day la call message/lifecycle contract
- chua bao gom signaling/WebRTC session

## 7. Sidebar / inbox

- sort giam dan theo `lastMessageAt`
- preview:
  - text -> text
  - sticker -> `Sticker`
  - image -> `Anh`
  - file -> `File - <ten file>`
  - poll -> cau hoi poll
  - revoke -> `Tin nhan da duoc thu hoi`

## 8. Non-goals hien tai

- signaling/WebRTC session
- read receipt chi tiet theo tung message
