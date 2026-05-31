# Flow loi va retry cua message

Tai lieu nay chot state machine toi thieu cho message trong UI va flow retry khi gui tin nhan bi loi trong `dialo-desktop`.

Tai lieu lien quan:
- `docs/message-realtime-flow.md`
- `docs/conversation-data-loading-flow.md`
- `docs/attachment-upload-flow.md`

## 0. Pham vi tai lieu

Tai lieu nay dong cho:
- state `uploading`
- state `sending`
- state `sent`
- state `failed`
- retry text
- retry sticker
- retry attachment
- hanh vi toi thieu khi REST hoac STOMP gap loi

Tai lieu nay chua dong cho:
- offline queue chinh thuc
- background resend tu dong
- exponential backoff
- dead-letter hoac idempotency key contract ben backend
- banner ket noi mang toan cuc

## 1. Message state machine

Frontend hien tai co 4 trang thai gui message:

- `uploading`
- `sending`
- `sent`
- `failed`

Y nghia:

### 1.1. `uploading`
- chi ap dung cho message co file/image can upload truoc
- file chua thanh attachment hop le de gui sang messaging
- UI duoc phep hien preview local

### 1.2. `sending`
- request gui message dang duoc thuc hien
- da co payload hop le de goi `POST /api/v1/messages` hoac `POST /api/v1/messages/direct`

### 1.3. `sent`
- UI da co server message hop le
- optimistic item da duoc thay bang item backend
- message nay khong con o retry mode

### 1.4. `failed`
- mot buoc trong flow gui message that bai
- user duoc phep bam retry
- UI khong duoc tu doi thanh `sent` neu khong co response thanh cong moi

## 2. Retry payload trong frontend

Moi optimistic message co the mang `retryPayload` de frontend biet cach gui lai.

Ba nhom chinh:

### 2.1. Text

```ts
{
  kind: 'text',
  content,
  reference?
}
```

### 2.2. Sticker

```ts
{
  kind: 'sticker',
  sticker: {
    id,
    url
  }
}
```

### 2.3. Attachment

```ts
{
  kind: 'attachment',
  files: File[]
}
```

Quy tac:
- `retryPayload` phai du de gui lai cung hanh vi ban dau
- retry khong tao local message moi
- retry dung lai chinh bubble da `failed`

## 3. Flow gui text

1. User submit text
2. Frontend tao optimistic message voi:
   - `sendStatus = "sending"`
   - `retryPayload.kind = "text"`
3. Append vao thread
4. Goi `POST /api/v1/messages`
5. Neu thanh cong:
   - thay optimistic item bang server item
   - `sendStatus` khong can giu nua hoac xem la da `sent`
6. Neu that bai:
   - doi message sang `failed`
   - giu nguyen bubble va noi dung de retry

## 4. Flow gui attachment

1. User chon file/image
2. Frontend tao optimistic message voi:
   - preview local
   - `sendStatus = "uploading"`
   - `retryPayload.kind = "attachment"`
3. Goi upload theo `docs/attachment-upload-flow.md`
4. Neu upload thanh cong:
   - doi `sendStatus` thanh `sending`
   - goi `POST /api/v1/messages`
5. Neu upload loi:
   - doi `sendStatus` thanh `failed`
6. Neu upload thanh cong nhung gui message loi:
   - doi `sendStatus` thanh `failed`

## 5. Flow gui sticker

1. User chon sticker
2. Frontend tao optimistic sticker message voi:
   - `sendStatus = "sending"`
   - `retryPayload.kind = "sticker"`
3. Goi `POST /api/v1/messages`
4. Neu thanh cong:
   - thay optimistic sticker item bang server item
5. Neu that bai:
   - doi sang `failed`

## 6. Flow retry

### 6.1. Retry text

1. User bam retry tren message `failed`
2. Frontend doi state sang `sending`
3. Goi lai `POST /api/v1/messages`
4. Neu thanh cong:
   - thay bubble cu bang server item
   - dedupe theo `message.id`
5. Neu loi:
   - doi lai `failed`

### 6.2. Retry sticker

1. User bam retry
2. Frontend doi state sang `sending`
3. Goi lai `POST /api/v1/messages` voi `stickers`
4. Neu thanh cong:
   - thay bubble cu bang server item
5. Neu loi:
   - doi lai `failed`

### 6.3. Retry attachment

1. User bam retry
2. Frontend doi state sang `uploading`
3. Upload lai file
4. Neu upload thanh cong:
   - doi sang `sending`
   - gui lai message voi `attachments`
5. Neu bat ky buoc nao loi:
   - doi sang `failed`

## 7. Quy tac UI khi failed

Khi message `failed`:
- bubble van o nguyen vi tri trong thread
- user van thay duoc content / sticker / preview attachment
- UI duoc phep hien icon loi hoac action retry
- khong duoc mat draft ma user vua gui

Khong duoc:
- xoa bubble that bai ngay lap tuc
- tao bubble moi khi retry, tru khi co quyet dinh doi UX sau nay

## 8. Quy tac UI khi retry

Khi retry:
- dung lai message bubble hien tai
- chi doi `sendStatus`
- khong doi React key
- khong append them optimistic bubble moi

Muc tieu:
- giu vi tri thread on dinh
- tranh duplicate message
- tranh duplicate key

## 9. STOMP va retry

STOMP khong duoc xem la co che retry.

Quy tac:
- retry la hanh vi do frontend chu dong goi lai REST
- neu message da duoc backend luu va STOMP da phat, frontend phai dedupe theo `message.id`
- khong duoc vi mat STOMP ma tu dong xem REST da loi
- khong duoc vi REST thanh cong ma append mu quang them 1 bubble nua khi STOMP ve sau

## 10. Truong hop loi toi thieu

### 10.1. Mat token hoac session het han
- REST gui message se loi
- frontend doi message sang `failed`
- neu app co flow refresh token thi phan auth xu ly rieng
- sau khi co session hop le lai, user moi retry bang tay

### 10.2. Upload attachment loi
- bubble attachment sang `failed`
- retry se upload lai tu dau

### 10.3. REST thanh cong nhung STOMP cham
- bubble van duoc thay bang server item qua response REST
- khi STOMP toi sau thi dedupe theo `message.id`

### 10.4. STOMP toi truoc, REST response toi sau
- thread co the da co server item tu STOMP
- khi REST response toi sau, frontend phai replace optimistic item va collapse duplicate

### 10.5. Mat ket noi STOMP nhung REST van song
- user van gui duoc message
- inbox/thread o view hien tai co the van cap nhat qua REST success
- sau reconnect STOMP, frontend nen refetch inbox va thread neu can

## 11. Checklist frontend

- Message text moi tao voi `sendStatus = "sending"`
- Message attachment moi tao voi `sendStatus = "uploading"`
- Sticker moi tao voi `sendStatus = "sending"`
- Bat ky loi nao trong flow gui deu dua bubble ve `failed`
- Retry khong tao bubble moi
- Retry dung lai `retryPayload`
- Replace optimistic item bang server item khi thanh cong
- Moi lan replace hoac append deu phai dedupe theo `message.id`

## 12. Checklist backend

- REST failure phai tra ma loi de frontend danh dau `failed`
- Server message thanh cong phai co `message.id` on dinh
- Neu backend da nhan request thanh cong thi thread event STOMP phai tham chieu cung `message.id`
- Khong nen phat 2 message logic khac nhau cho cung 1 user action gui tin
