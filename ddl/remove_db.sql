

file -inlinebatch END_OF_BATCH

DROP PROCEDURE CreateToken IF EXISTS;
DROP PROCEDURE UseToken IF EXISTS;
drop view user_error_events_summary if exists;
drop table token_users if exists;
drop table secure_token_table if exists;
drop table user_recent_transactions if exists;
drop stream user_error_events if exists;

END_OF_BATCH

