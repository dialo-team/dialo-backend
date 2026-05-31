# Description UI -> du lieu backend can tra

Tai lieu nay khong mo ta pixel/UI chi tiet.
Muc tieu cua tai lieu nay la:
- frontend mo ta nhung khu vuc dang hien thi tren UI
- voi moi khu vuc, liet ke du lieu backend can tra de frontend render dung
- neu mot khu vuc da "giu nguyen nhu hien tai", tai lieu nay bo sung cac field can co de backend biet can tra gi

Nguyen tac:
- frontend co the doi cach sap xep/layout nho, nhung cac field cot loi ben duoi van can de render dung nghiep vu
- uu tien tra du du lieu trong REST bootstrap/detail
- STOMP event nen tra du payload de frontend co the upsert theo `conversationId` hoac `message.id`
- field nao la requester-specific thi backend can tinh theo current user

Tai lieu lien quan:
- `docs/frontend-messaging-integration-checklist.md`
- `docs/conversation-api-contract.md`
- `docs/message-realtime-flow.md`
- `docs/message-rendering-contract.md`
- `docs/messaging-frontend-integration-status.md`
- `docs/frontend-messaging-ui-behavior-spec.md`
- `docs/messaging-phase1-backend-handoff.md`

## 1. Danh sach cuoc tro chuyen

Moi item conversation tren sidebar hien tai can du lieu sau:
- `conversationId`
- `type`: `DIRECT` | `GROUP`
- ten hien thi:
  - direct: `counterpartName`
  - group: `groupName`
- avatar hien thi:
  - direct: `counterpartAvatarUrl`
  - group: `groupAvatarUrl`
- preview tin nhan cuoi:
  - `lastMessage`
  - hoac `lastMessagePreview`
  - hoac du payload cuoi de frontend tu tao preview
- sender cua tin nhan cuoi neu can prefix o group:
  - `lastMessageSenderId`
  - `lastMessageSenderName` neu backend co
- loai tin nhan cuoi de tao preview dung:
  - `lastMessageType`
  - `lastMessageSystem`
  - neu preview khong du, backend nen tra them payload toi thieu cua last message
- thoi gian sap xep/hien thi:
  - `lastMessageAt`
- so tin chua doc theo current user:
  - `unreadCount`

Backend can dam bao:
- inbox refetch hoac inbox event luon dong bo duoc cac field tren
- `lastMessageAt` la nguon sort giam dan
- `unreadCount` la requester-specific

## 2. Khi mo mot cuoc tro chuyen

Frontend can co du lieu de render 3 lop:
- shell conversation
- thread message
- panel thong tin ben phai

### 2.1. Shell conversation / header

Can du lieu:
- `conversationId`
- `type`
- ten hien thi:
  - direct: `counterpartName`
  - group: `groupName`
- avatar:
  - direct: `counterpartAvatarUrl`
  - group: `groupAvatarUrl`
- direct relation state de biet co phai "Nguoi la" hay khong:
  - co the tra truc tiep `relationStatus`
  - hoac du metadata de frontend suy ra
- group member count:
  - `activeMemberCount`
  - field nay can tinh theo active member, khong tinh member da roi nhom
- group owner/admin state cua current user:
  - `myRole`: `OWNER` | `ADMIN` | `MEMBER`
- direct counterpart id / group id de cac action khac dung tiep:
  - `counterpartId` voi direct
  - `conversationId` voi group

Neu header co action goi/video/them thanh vien/tim kiem/panel thong tin thi backend khong can tra button config,
nhung phai tra du du lieu de frontend biet:
- direct hay group
- current user co phai active member hay khong
- myRole neu action phu thuoc quyen

### 2.2. Khu ghim tin nhan

De render khu pinned messages nhu hien tai, backend can tra tren tung `MessageResponse`:
- `id`
- `conversationId`
- `senderId`
- `senderName`
- `content`
- `attachments`
- `stickers`
- `reference`
- `poll`
- `call`
- `pinned`
- `createdAt`
- `deleted`
- `deletedForEveryone`
- `deletedTimeStamp`

Can them quy tac:
- backend can dam bao toi da 3 message `pinned = true` trong mot conversation
- neu vuot qua, backend nen tra loi business error ro rang de frontend thong bao

### 2.3. Thread message

De render day du bubble trong thread, backend can tra cho moi message:
- identity:
  - `id`
  - `conversationId`
  - `position`
- sender:
  - `senderId`
  - `senderName`
  - neu can avatar trong future thi co the tra them `senderAvatarUrl`
- message kind payload:
  - `type`
  - `system`
  - `content`
  - `attachments`
  - `stickers`
  - `reference`
  - `poll`
  - `call`
  - `mentions`
  - `mentionEveryone`
- status/time:
  - `createdAt`
  - `editedTimeStamp`
  - `deleted`
  - `deletedForEveryone`
  - `deletedTimeStamp`
- metadata de render hanh vi bubble:
  - `pinned`
  - `reactions`
  - `readBy`

Backend can dam bao:
- `message.id` on dinh giua REST response va STOMP thread event
- thread event cho reaction/pin/revoke/poll update phai tra lai cung `message.id`
- requester-specific fields nhu `readBy`, `selectedAnswerIds` can du nghia voi requester

## 3. Reaction

UI hien tai can reaction de hien thi bang icon/emoji that, khong phai ma so thu.

Backend can tra cho moi reaction summary:
- `emoji`: gia tri display-ready de frontend render truc tiep
  - vi du: `"👍"` thay vi chi tra `128077`
- `count`
- `me`

Neu backend muon luu internals bang code point/enum, van nen tra them cho frontend it nhat mot trong hai cach:
- cach uu tien:
  - `emoji`: chuoi emoji render-ready
- cach thay the:
  - `emojiCode`: ma so on dinh
  - frontend co bang map rieng tu code -> icon

De tranh lech frontend/backend, backend nen uu tien tra:
```json
{
  "emoji": "👍",
  "count": 3,
  "me": true
}
```

Neu co modal chi tiet reaction theo user trong future, backend se can them:
- `userId`
- `displayName`
- `avatarUrl`

## 4. Reply / reference

UI hien tai co preview message duoc reply.
Backend can tra:
- `reference.messageId`
- `reference.conversationId`
- `reference.exist`

Neu muon frontend render preview day du hon ma khong phai tu tim lai local, backend co the tra them:
- `referencePreview`
  - `content`
  - `attachments`
  - `stickers`
  - `senderId`
  - `senderName`

Khong bat buoc ngay neu frontend da tu lookup duoc message goc trong thread.

## 5. Read receipt

UI hien tai can biet ai da doc de hien:
- direct: trang thai `Da xem`
- group: so nguoi da xem

Backend can tra tren message:
- `readBy[]`
  - `userId`
  - `displayName`
  - `avatarUrl`

Can quy uoc:
- `readBy` la danh sach user da doc toi it nhat message do
- `readBy` la requester-visible data
- sau `PUT /read`, backend nen dong bo lai bubble cuoi qua thread event hoac thread refetch

## 6. Poll

Poll tren UI hien tai can:
- `message.id`
- `conversationId`
- `senderId`
- `poll.question`
- `poll.answers[]`
  - `id`
  - `content`
- `poll.allowMultiSelect`
- `poll.expiry`
- `poll.results.isFinalized`
- `poll.results.answerCounts[]`
  - `answerId`
  - `count`
- `poll.results.selectedAnswerIds[]`

Neu UI can xem danh sach voter theo tung dap an, backend can co endpoint/response tra:
- `conversationId`
- `messageId`
- `answerId`
- `total`
- `voters[]`
  - `userId`
  - `nick` hoac `displayName`
  - `avatarUrl`

## 7. Call item

Neu thread co bubble call, backend can tra:
- `call.type`: `AUDIO` | `VIDEO`
- `call.status`: `ONGOING` | `ENDED`
- `call.users[]`
  - `userId`
  - `nick`
  - `avatarUrl`
- `call.startedTime`
- `call.endedTime`

## 8. Panel thong tin cuoc tro chuyen

### 8.1. Panel thong tin chung

Can du lieu:
- avatar hien thi
- ten hien thi
- `activeMemberCount` neu la group
- `myRole`
- `description` neu co
- `createdAt` neu UI muon hien thi sau nay

Neu UI hien "Nguoi la" o direct thi backend can tra:
- `relationStatus`

### 8.2. Panel nhom chung

Frontend can du lieu cac nhom chung giua current user va counterpart:
- `conversationId`
- `groupName`
- `groupAvatarUrl`
- `activeMemberCount`
- `myMembershipRole` neu backend muon phan biet tab "Nhom cua toi"
- `joinedAt` hoac `createdAt` neu sau nay muon sort on dinh
- `status` neu group co the bi dissolve/hidden

UI hien tai dang dung cac hanh vi sau:
- co tong so nhom chung de hien o header/info panel
- co list nhom chung
- co tab:
  - `Tat ca`
  - `Nhom cua toi`
- co search theo ten nhom
- co che do chon nhieu
- co nut cuoi panel:
  - neu khong chon nhom nao: "Them vao nhom"
  - neu da chon >= 1 nhom: "Xoa khoi nhom"

De backend support dung nghia cua panel nay, can tach ro 2 loai du lieu:

1. Du lieu render list:
- `conversationId`
- `groupName`
- `groupAvatarUrl`
- `activeMemberCount`
- `myMembershipRole`

2. Du lieu hanh vi:
- co/khong cho current counterpart duoc them vao group do:
  - `canInviteCounterpart`
- co/khong cho current counterpart duoc remove khoi group do:
  - `canRemoveCounterpart`

Neu backend khong tra du permission/action state, frontend chi co the mo ta UI va bat user thao tac thu,
khong the biet truoc nhom nao hop le de invite/remove.

Neu backend chua co danh sach nay thi panel nay se chi la placeholder/mock.

### 8.3. Panel kho luu tru

Panel nay dang chia theo media/file/link va search.
De render dung tu thread data, backend can dam bao trong message payload:
- attachments co `contentType`, `fileName`, `size`, `url`
- text content van co trong `content` de frontend trich link
- `createdAt`
- `senderId`
- `senderName`

Neu muon backend ho tro manh hon ve search/filter archive trong future, co the can endpoint rieng.
Hien tai frontend van co the suy ra tu thread payload neu du message data.

### 8.4. Bang tin nhom

UI hien tai can:
- notes
- pinned messages
- polls

Voi pinned messages:
- dung message data nhu muc 2.2

Voi polls:
- dung poll data nhu muc 6

Voi notes:
- neu backend muon support note that, can co it nhat:
  - `noteId`
  - `conversationId`
  - `authorUserId`
  - `authorName`
  - `authorAvatarUrl`
  - `content`
  - `contentPreview`
  - `createdBy`
  - `createdByName`
  - `createdAt`
  - `updatedAt`
  - `canEdit`
  - `canDelete`

UI hien tai dang doc/ky vong:
- note card trong bulletin:
  - avatar tac gia
  - ten tac gia
  - nhan loai "Ghi chu"
  - preview noi dung 2 dong
  - thoi gian tao
  - action xem chi tiet
- note detail modal:
  - ten tac gia
  - thoi gian tao
  - noi dung day du
  - action chinh sua
- note edit mode:
  - noi dung hien tai
  - action luu

Neu backend muon frontend khong phai suy luan them, note item nen co shape toi thieu:
```json
{
  "noteId": "note-001",
  "conversationId": "group-001",
  "authorUserId": "user-001",
  "authorName": "Nguyen Van A",
  "authorAvatarUrl": "https://...",
  "content": "Noi dung day du",
  "contentPreview": "Noi dung rut gon de list",
  "createdAt": "2026-05-31T08:00:00Z",
  "updatedAt": "2026-05-31T08:10:00Z",
  "canEdit": true,
  "canDelete": true
}
```

Neu backend chua support note, frontend chi nen giu mock/no-op ro rang.

### 8.5. Quan ly nhom

UI hien tai can biet:
- `myRole`
- danh sach active members
- owner hien tai
- danh sach admin/deputy hien tai
- join link settings / member approval settings neu backend support that

De render va thao tac dung, backend can tra hoac cho phep frontend lay du:
- `ownerId`
- `members[]`
  - `userId`
  - `displayName`
  - `avatarUrl`
  - `role`: `OWNER` | `ADMIN` | `MEMBER`
  - `joinedAt`
  - `leftAt` hoac `active`

Quan trong:
- frontend can danh sach `active members` that
- user da `leftAt != null` khong duoc xuat hien trong danh sach them thanh vien, deputy, owner transfer, member count

### 8.6. Chan khoi nhom

Neu UI can panel blocked members that, backend can tra:
- `blockedMembers[]`
  - `userId`
  - `displayName`
  - `avatarUrl`
  - `blockedAt`
  - `blockedBy`
  - `role` neu can hien boi canh group role
  - `isFriend` neu muon giu hanh vi dong nhat voi member list

UI hien tai dang can:
- tong so member bi chan
- list member bi chan
- avatar
- ten
- action `Bo chan`
- nut `Them vao danh sach chan`

De khong phai dung state local/mock, backend can co it nhat:
- list blocked members hien tai
- action block member
- action unblock member
- chi `OWNER` va `ADMIN` moi duoc block/unblock member

Business rule da chot:
- blocked member van tim thay group trong danh sach conversation neu ho da tung la member cua group
- blocked member khong duoc xem noi dung tin nhan
- blocked member khong duoc xem thong tin nhom va cac panel lien quan
- blocked member khong duoc thuc hien thao tac trong group
- khi mo group, UI chi nen thay trang thai/toast/man hinh cho biet current user da bi chan
- blocked member khong duoc nhan thread content, bulletin content, member detail, archive detail cua group

Vi vay backend can co co che requester-specific cho group detail:
- neu current user dang bi block trong group:
  - van co the tra `conversationId`, `groupName`, `groupAvatarUrl`
  - nen tra them `blocked = true`
  - nen tra them `blockedReason` hoac `blockedMessage` neu muon frontend hien thong diep ro hon
  - khong tra `messages`
  - khong tra `members` chi tiet
  - khong tra `bulletin/notes/polls/archive` detail
  - khong cho phep send message, reaction, pin, vote, add member, leave management action theo nghia thong thuong

Suggested minimal blocked detail shape:
```json
{
  "conversationId": "group-001",
  "type": "GROUP",
  "groupName": "Nhom hoc tap",
  "groupAvatarUrl": "https://...",
  "blocked": true,
  "blockedMessage": "Ban da bi chan khoi nhom nay"
}
```

Neu chua support block member that thi frontend khong nen xem du lieu local/mock la source of truth.

### 8.7. Truong nhom / pho nhom

UI hien tai can:
- 1 owner
- 0..n deputy/admin

Backend can tra trong member list:
- `role = OWNER`
- `role = ADMIN`
- `role = MEMBER`

Frontend khong nen tu suy role neu backend khong tra ro.

## 9. Them thanh vien vao nhom

De modal them thanh vien hoat dong dung, backend can giup frontend co du 2 lop du lieu:

1. danh sach candidate co the them:
- co the lay tu direct conversations/contact list hien co

2. danh sach active members hien tai cua group:
- de loai bo user da o trong nhom

Toi thieu backend can dam bao o group detail hoac endpoint members:
- `members[]`
  - `userId`
  - `displayName`
  - `avatarUrl`
  - `role`
  - `isFriend`
  - `leftAt` hoac `active`

Ngoai ra candidate them thanh vien tu danh ba/direct list nen co:
- `userId`
- `displayName`
- `avatarUrl`
- `relationStatus`
- `isBlocked`
- `isActive` neu account co the bi khoa/an

Frontend hien tai dang loai bo candidate dua tren `active members`.
Neu backend khong tra `members[]` that, frontend se khong biet user nao dang o trong nhom.

Neu backend chi tra summary group ma khong tra `members[]`, frontend se khong the biet chac user nao dang la active member,
dan den viec modal add member co the hien nham user da o trong nhom.

## 10. Hien deputy / ha deputy

De frontend render va thao tac role dung, backend can tra:
- owner hien tai
- active members hien tai
- role hien tai cua tung member

Toi thieu:
- `ownerId`
- `members[]`
  - `userId`
  - `displayName`
  - `avatarUrl`
  - `role`
  - `active`

Neu sau khi doi role ma inbox/thread/group detail khong dong bo lai member list, frontend se thay "ha deputy chua duoc".

## 11. Roi nhom / roi nhom voi owner

### 11.1. Member/Admin leave

De leave group dung, backend can:
- chap nhan `POST /leave`
- sau thanh cong, group khong con xuat hien trong inbox cua requester
- active member count giam
- member list khong con user do trong active members

### 11.2. Owner leave

UI hien tai chua co flow owner leave that.
Backend can chot mot trong 2 huong:

1. Khong cho owner roi nhom:
- `POST /leave` tra business error ro rang
- frontend tiep tuc an/disable flow owner leave

2. Cho owner roi nhom sau khi transfer owner:
- backend can co `PUT /owner`
- backend can tra du member list active de frontend chon owner moi
- owner cu xuong `ADMIN` hoac roi nhom tuy business rule

Toi thieu backend can chot ro:
- owner co duoc leave hay khong
- neu co, can transfer owner truoc hay backend tu tuyen owner moi

Neu chua chot rule nay, frontend khong the hoan thien owner leave flow.

## 12. Giai tan nhom

De dissolve group dung, backend can:
- chi cho `OWNER`
- sau thanh cong, group bien mat khoi inbox cua tat ca active members
- realtime/inbox refetch phan anh ngay thay doi do

Can field/trang thai de frontend suy ra:
- group da bi xoa/giai tan
- requester khong con la active member

## 13. Contact

Trong phan contact, UI hien tai can:
- danh sach ban be / direct
- danh sach group
- loi moi ket ban
- loi moi vao nhom

Can tach ro tung nhom du lieu:

### 13.1. Danh sach ban be

Moi friend item can:
- `userId`
- `displayName`
- `avatarUrl`
- `classificationTags[]` neu contact list co phan loai
- `lastActiveAt` neu muon sort/future presence

### 13.2. Danh sach nhom

Moi group item can:
- `conversationId`
- `groupName`
- `groupAvatarUrl`
- `membersCount`
- `lastActiveAt`
- `isManagedByMe`
- `classificationTags[]`

### 13.3. Loi moi ket ban da nhan

Moi received friend request can:
- `requestId`
- `fromUserId`
- `displayName`
- `avatarUrl`
- `reason`
- `requestedAt`
- `canAccept`
- `canReject`
- `canOpenChat`

UI hien tai dang render:
- avatar
- ten
- noi dung ly do hoac fallback theo `requestedAt`
- action `Chap nhan`
- action `Tu choi`
- icon mo chat

### 13.4. Loi moi ket ban da gui

Moi sent friend request can:
- `requestId`
- `toUserId`
- `displayName`
- `avatarUrl`
- `reason`
- `requestedAt`
- `canCancel`
- `canOpenChat`

UI hien tai dang render:
- avatar
- ten
- noi dung ly do hoac fallback theo `requestedAt`
- action `Huy yeu cau`
- icon mo chat

### 13.5. Loi moi vao nhom

Moi group invite can:
- `inviteId`
- `conversationId`
- `groupName`
- `groupAvatarUrl`
- `invitedByUserId`
- `invitedByName`
- `invitedAt`
- `canAccept`
- `canReject`

UI hien tai dang render:
- avatar nhom
- ten nhom
- action `Dong y`
- action `Tu choi`

Neu backend muon UI du nghia hon, nen tra them:
- `membersCount`
- `description`
- `inviteMessage`

### 13.6. Modal thong tin contact

Khi mo modal profile tu contact list, frontend can:
- `id`
- `displayName`
- `avatarUrl`
- `type`: `direct` | `group`
- `isFriend` neu la direct
- `canOpenChat`

UI hien tai dang hien:
- avatar
- ten
- badge text:
  - `Nhom`
  - `Ban be`
  - `Nguoi la`
- nut `Nhan tin`

## 14. Thong tin tai khoan cua current user

UI hien tai can:
- `userId`
- `displayName`
- `avatarUrl`
- `gender`
- `birthDay`
- `birthMonth`
- `birthYear`

UI hien tai dang co 2 state:

### 14.1. View mode

Can:
- `displayName`
- `avatarUrl`
- `gender`
- `birthDay`
- `birthMonth`
- `birthYear`

Dang hien thi:
- avatar
- ky tu dau ten neu chua co avatar that
- ten hien thi
- gioi tinh
- ngay sinh

### 14.2. Edit mode

Can:
- gia tri hien tai cua:
  - `displayName`
  - `gender`
  - `birthDay`
  - `birthMonth`
  - `birthYear`
- rang buoc validate:
  - ten khong rong
- neu backend co validate rule khac, nen tra ro de frontend dong bo:
  - do dai ten
  - ky tu khong hop le
  - rang buoc ngay sinh neu co

Neu muon realtime cho profile cua chinh current user, backend can co co che refetch/event de cap nhat:
- ten
- avatar
- cac setting hien thi lien quan

## 14.1. Field map de backend de doi chieu nhanh

### Current user account
- `userId`
- `displayName`
- `avatarUrl`
- `gender`
- `birthDay`
- `birthMonth`
- `birthYear`

### Contact profile
- `id`
- `displayName`
- `avatarUrl`
- `type`
- `isFriend`

### Friend request received
- `requestId`
- `fromUserId`
- `displayName`
- `avatarUrl`
- `reason`
- `requestedAt`

### Friend request sent
- `requestId`
- `toUserId`
- `displayName`
- `avatarUrl`
- `reason`
- `requestedAt`

### Group invite
- `inviteId`
- `conversationId`
- `groupName`
- `groupAvatarUrl`
- `invitedByUserId`
- `invitedByName`
- `invitedAt`

## 15. Realtime backend can dong bo gi

### 15.1. Inbox realtime

Can du de frontend cap nhat hoac refetch:
- conversation moi
- last message thay doi
- unreadCount thay doi
- group metadata thay doi neu anh huong sidebar

### 15.2. Thread realtime

Can du de frontend upsert theo `message.id`:
- message moi
- reaction update
- pin/unpin
- revoke
- poll update
- readBy update

### 15.3. Group metadata realtime

Neu backend muon UI group management on dinh, can co cach dong bo lai:
- member list
- role changes
- owner changes
- activeMemberCount

Neu chua co topic rieng, it nhat inbox event/refetch conversation detail phai du de frontend lay lai du lieu moi.

## 16. Cac diem backend dang can chot de frontend render dung

1. Reaction response:
- nen tra emoji display-ready, khong chi tra ma so

2. Group detail/member data:
- can co danh sach active members that
- can co role cua tung member
- can biet ai da roi nhom

3. Owner leave rule:
- block hoan toan
- hoac bat buoc transfer owner truoc

4. Notes data:
- neu muon support that thi can contract rieng cho note item/list/detail

5. Common groups:
- neu muon panel nay dung du lieu that thi can endpoint/list data that

6. Blocked members:
- neu muon panel nay dung du lieu that thi can member block contract that

## 17. Backend co the lam ngay gi neu chi doc file nay

Neu backend chua muon doc het code frontend, co the dung file nay de chot ngay:

### 17.1. Response shape can uu tien sua ngay
- `GET /api/v1/conversations`
  - du summary sidebar
- `GET /api/v1/conversations/{conversationId}`
  - du header + thread + group metadata
- `MessageResponse`
  - du field cho bubble, reaction, read receipt, poll, pin, revoke
- group detail/member data
  - du active members + role + owner
- contact/account related responses
  - du field cho modal/profile/list

### 17.2. Cac loi hien tai backend co the sua dua tren file nay
1. reaction dang tra ma so thay vi display-ready emoji
2. group detail chua du `members[]` nen frontend khong biet ai dang o trong nhom
3. role/member state chua du nen ha deputy/add member de sai
4. owner leave chua co rule ro rang
5. common groups/blocked members/notes neu muon dung du lieu that thi chua du contract

## 18. 4 quyet dinh backend can chot de frontend khong phai hoi lai

### 18.1. Owner leave

Da chot:
- owner phai transfer owner truoc roi moi leave

Backend can tra loi ro rang neu request khong hop le.

### 18.2. Notes

Da chot:
- notes support that
- backend se cap contract/list/detail/create/update/delete cho note item

### 18.3. Common groups actions

Da chot:
- co the moi counterpart vao nhom chung neu business validation cho phep
- khong cho xoa counterpart khoi nhom chung, tru `OWNER` va `ADMIN`

Backend nen tra requester-specific permission state:
- `canInviteCounterpart`
- `canRemoveCounterpart`

Goi y rule:
- member thuong:
  - co the thay nhom chung
  - khong duoc remove counterpart khoi nhom
- owner/admin:
  - co the remove counterpart khoi nhom neu counterpart dang la active member

### 18.4. Blocked members

Da chot:
- chi `OWNER` va `ADMIN` moi duoc block/unblock
- blocked member van thay group trong danh sach
- blocked member khong duoc xem noi dung hay thong tin chi tiet cua group
- blocked member khong duoc thao tac gi trong group

Neu backend chot 4 diem nay va tra du field trong file nay, frontend co the bam rat sat vao contract ma khong phai suy doan them.
