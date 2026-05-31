# Contract render message

Tai lieu nay chot contract render message toi thieu cho frontend trong `dialo-desktop`.

Muc tieu:
- frontend khong suy luan UI chi tu `type`
- frontend render dua tren payload that cua message
- backend va frontend co cung cach hieu ve `SYSTEM`, `DEFAULT`, `content`, `attachments`, `stickers`, `reference`, `poll`, `call`, `mentions`

Tai lieu lien quan:
- `docs/conversation-realtime-flow.md`
- `docs/message-realtime-flow.md`
- `docs/message-error-retry-flow.md`
- `docs/attachment-upload-flow.md`

## 0. Pham vi tai lieu

Tai lieu nay dong cho:
- quy tac phan loai `SYSTEM` va `DEFAULT`
- quy tac render text
- quy tac render image / file / sticker
- quy tac render reply
- quy tac render poll
- quy tac render system event
- quy tac render mentions
- quy tac preview tin nhan o sidebar

Tai lieu nay chua dong cho:
- edit / delete
- read receipt theo tung bubble
- layout pixel-perfect cua tung widget

## 1. Nguyen tac tong quat

Frontend khong duoc render message chi dua vao `type`.

Quy tac dung:
- `type` chi cho biet message thuoc nhom nao o backend
- giao dien phai dua vao payload that de quyet dinh bubble hien gi

Nhom chinh:
- `SYSTEM` -> render system message
- `DEFAULT` -> render theo payload that cua item

Payload can doc:
- `content`
- `attachments`
- `stickers`
- `reference`
- `poll`
- `call`
- `mentions`
- `mentionEveryone`
- `reactions`
- `pinned`
- `deleted`
- `deletedTimeStamp`
- `editedTimeStamp`

## 2. Rule phan loai message

### 2.1. System message

Neu:
- `type = SYSTEM`
hoac
- `system = true`

Thi frontend render nhu system message:
- can giua thread
- khong render avatar sender theo kieu tin nhan thuong
- khong render nhu bubble chat cua `me/them`

### 2.2. User message

Neu khong phai system message thi xem la user message.

User message co the do:
- `me`
- `them`

Frontend xac dinh `me/them` dua tren `senderId` so voi `currentUserId`.

### 2.3. Revoked message

Neu:
- `deleted = true`
hoac
- `deletedTimeStamp != null`

va:
- `deletedForEveryone != true`

Thi frontend render nhu message da thu hoi:
- giu vi tri bubble cu trong thread
- khong render attachment/sticker/poll/call cu
- hien placeholder `Tin nhan da duoc thu hoi`

### 2.4. Deleted for everyone

Neu:
- `deleted = true`
- `deletedForEveryone = true`

Thi frontend render nhu message da bi xoa cho moi nguoi:
- giu vi tri bubble cu trong thread
- khong render attachment/sticker/poll/call cu
- hien placeholder `Tin nhan da bi xoa`

### 2.5. Edited message

Neu `editedTimeStamp != null`:
- frontend van render bubble goc theo payload hien tai
- frontend duoc phep hien dau hieu nho kieu `da chinh sua`
- khong tao system message rieng cho hanh vi edit

## 3. Rule render message `DEFAULT`

Message `DEFAULT` co the co mot hoac nhieu thanh phan trong cung mot item.

Frontend can render theo to hop payload, khong tach thanh nhieu message gia.

### 3.1. Chi co `content`

Render:
- text bubble thuong
- co highlight mention neu co `mentions`

### 3.2. Chi co `attachments`

Neu attachment la image:
- render image bubble

Neu attachment khong phai image:
- render file bubble

### 3.3. Chi co `stickers`

Render:
- sticker message
- khong can text bubble neu `content` rong

### 3.4. `content + attachments`

Render:
- mot message item
- phan text nam cung item voi media/file

Khong duoc:
- tach thanh 2 bubble doc lap chi vi co ca text va attachment

### 3.5. `content + reference`

Render:
- mot message reply
- phan preview cua message goc o tren
- phan noi dung moi o duoi

### 3.6. `poll`

Render:
- poll item trong thread
- khong render poll nhu text thuong

### 3.7. `call`

Neu backend gui `call`:
- render call item
- khong ep render nhu text thuong
- co the render:
  - `type`: `AUDIO` hoac `VIDEO`
  - `status`: `ONGOING` hoac `ENDED`
  - danh sach user tham gia
  - `startedTime`
  - `endedTime`

## 4. Rule render attachment

### 4.1. Image attachment

Attachment duoc xem la image khi:
- `contentType` bat dau bang `image/`

Render:
- image gallery neu co nhieu anh
- image item don neu chi co 1 anh

Field can dung:
- `id`
- `url`
- `width`
- `height`
- `contentType`

### 4.2. File attachment

Attachment duoc xem la file khi:
- `contentType` khong bat dau bang `image/`

Render:
- ten file
- kich thuoc file neu co
- action mo / tai xuong neu co `url`

Field can dung:
- `id`
- `fileName`
- `fileSize`
- `contentType`
- `url`

## 5. Rule render sticker

Sticker khong phai attachment file/image thong thuong.

Render:
- su dung danh sach `stickers`
- bubble co the dung style rieng

Field can dung:
- `id`
- `url`

Neu message co `stickers`:
- uu tien render nhu sticker message
- khong doi sticker thanh attachment image gia

## 6. Rule render reply

Neu message co `reference`:
- render preview cua message goc
- neu `reference.exist = true` thi preview message goc duoc phep hien text/image/file tom tat
- neu `reference.exist = false` thi hien trang thai message goc khong con ton tai

Quy tac:
- `reference` khong bien message thanh `type = REPLY` o frontend
- frontend van render no nhu mot `DEFAULT` message co them reply preview

## 7. Rule render mentions

Neu message co `mentions`:
- frontend highlight cac ten duoc mention trong `content`

Neu `mentionEveryone = true`:
- frontend duoc phep hien style dac biet cho `@All`

Quy tac:
- mention la metadata cua text block
- mention khong tao ra bubble rieng

## 8. Rule render poll

Neu message co `poll`:
- render poll item
- dung payload poll that de hien:
  - cau hoi
  - danh sach lua chon
  - so luot vote
  - cho phep chon nhieu hay khong
  - han ket thuc neu co

Khong duoc:
- render poll chi bang `content` string neu payload poll da co cau truc day du

## 9. Rule render system event

System event toi thieu can ho tro:
- `Hai ban da tro thanh ban be`
- user join event
- event mang tinh chat thong bao he thong khac

Render:
- style system message
- uu tien text event o giua thread

Quy tac:
- system event co the co `text`
- system event co the co payload event map sang `actor/action/icon`
- frontend khong render avatar hoac bubble me/them cho system event

## 10. Rule mapping sang block UI

Frontend co the map message thanh cac block de render:

- `text`
- `image`
- `file`
- `poll`

Quy tac map toi thieu:
- `content` -> text block
- `attachments image/*` -> image block
- `attachments non-image` -> file block
- `stickers` -> image block voi `isSticker = true`
- `poll` -> poll block

Neu message da thu hoi:
- khong map block media/file/sticker/poll tu payload cu
- uu tien render placeholder revoked/delete neu message da bi thay doi

## 10.1. Rule render reaction

Neu message co `reactions`:
- render nhom reaction theo `emoji`
- hien `count`
- neu `me = true` thi reaction cua current user can co state active

## 10.2. Rule render read receipt

Neu message co `readBy`:
- frontend co the render read receipt nho o bubble cuoi hoac bubble da duoc doc
- moi item gom:
  - `userId`
  - `displayName`
  - `avatarUrl`
  - `readAt`

Quy tac:
- `readBy` khong bao gom sender cua chinh message do
- `readBy` la aggregate theo `lastReadPosition`, khong phai log read-event rieng

Quy tac:
- reaction la metadata cua message, khong tao bubble rieng
- khi thread event ve cung `message.id`, frontend update lai nhom reaction tren bubble cu

## 10.2. Rule render pinned

Neu `pinned = true`:
- frontend hien dau hieu nho message dang duoc ghim

Phase hien tai:
- chua can pinned tray hoac pinned panel rieng
- pin/unpin chi la state cua bubble message

Muc tieu:
- mot message item co the co nhieu block
- render theo block nhung van giu 1 message bubble logic

## 11. Rule preview o conversation list

Sidebar preview khong nen suy luan chi tu `type`.

Uu tien:
1. dung `lastMessage` hoac `lastMessagePreview` backend tra ve
2. neu backend khong tra du, frontend moi fallback theo payload message gan nhat

Fallback toi thieu:
- sticker -> `Sticker`
- image attachment -> `Anh`
- file attachment -> `File - <ten file>`
- poll -> cau hoi poll
- system event -> text system event
- text thuong -> `content`

## 12. Tranh suy luan sai

Khong duoc:
- thay `type = DEFAULT` roi mac dinh render text-only
- thay co attachment roi bo qua `content`
- thay co `reference` roi tach thanh bubble doc lap
- thay system event roi render nhu tin nhan cua nguoi gui
- thay sticker roi doi thanh image attachment binh thuong neu muon giu UX rieng

## 13. Checklist frontend

- `SYSTEM` hoac `system = true` -> render system message
- `DEFAULT` -> render dua tren payload that
- `content` -> text block
- `attachments image/*` -> image block
- `attachments non-image` -> file block
- `stickers` -> sticker block
- `reference` -> reply preview
- `poll` -> poll item
- `mentions` -> highlight text
- `mentionEveryone` -> style `@All`
- `reactions` -> reaction bar theo bubble
- `pinned = true` -> badge ghim nho tren bubble
- `deleted = true` va `deletedForEveryone = true` -> placeholder delete for everyone
- `deleted = true` hoac `deletedTimeStamp != null` -> placeholder revoked
- `editedTimeStamp != null` -> co the hien indicator edited nho
- sidebar preview uu tien dung summary backend

## 14. Checklist backend

- user message thuong luu voi `type = DEFAULT`
- system event luu voi `type = SYSTEM` hoac `system = true`
- khi co `content + attachments`, giu ca hai trong cung mot message
- khi la sticker message, tra `stickers`
- khi la reply, tra `reference`
- khi la poll, tra `poll`
- khi la file/image, tra `attachments` dung `contentType`
- inbox summary nen co `lastMessage` hoac `lastMessagePreview` de frontend khong phai doan
