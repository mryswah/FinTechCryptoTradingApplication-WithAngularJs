INSERT INTO "USER"(username,password)
VALUES ('user','password');

INSERT INTO "USER"(username,password)
VALUES ('user1','password1');

INSERT INTO "USER_WALLET"("USER_ID","SYMBOL","ACCOUNTBALANCE")
VALUES ((SELECT id FROM "USER" WHERE username = 'user'), 'USDT', '50000.00');