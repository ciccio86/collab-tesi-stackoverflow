#!/bin/bash
mysql << EOF

use stackoverflow_march;

# Increment the truncate limit of GROUP_CONCAT function for this session
SET SESSION group_concat_max_len = 100000;

###################################################################################################
# This query take all questions and saves in file '/tmp/raw_questions.csv' with the following header:
#
# - QuestionID: the Id of the question post in StackOverflow;
# - CreationDate: the creation date of the question;
# - Title: the title of the question;
# - Body: the full text of the question where the characters '\' and '"' are escaped by the character '\';
# - Tags: the tags associated to the question;
# - AcceptedDate: the date in which an answer to the question was voted as Accepted, or Null there's no accepted answer;
# - NumberOfComments: the number of comments posted by the asker to his own question before acceptance date;
# - CommentsTexts: the concatenation of all comments posted by the asker to his own question before acceptance date (separated by the character ' ');
# - Successful: wether there was an accepted answer or not.
#
###################################################################################################
SELECT 'QuestionID', 'CreationDate', 'Title', 'Body', 'Tags', 'AcceptedDate', 'NumberOfComments', 'CommentsTexts', 'Successful'

UNION ALL

SELECT
 ##ucq.c_Id AS CommentID,
  q.q_postID AS QuestionID,
  q.q_postDate AS CreationDate,
  q.q_title AS Title,
  q.q_body AS Body,
  q.q_tags AS Tags,
  ##ucq.userId AS UserId,
  ##ucq.c_ts_creationDate AS CommentDate,
  ##q.q_acceptedAnswerId AS AcceptedAnswerId,
  COALESCE(v.CreationDate, 'Null') AS AcceptedDate,
  IF(ucq.q_Id, COUNT(*), 0) AS NumberOfComments,
  COALESCE(GROUP_CONCAT(ucq.c_text SEPARATOR ' '), '') AS CommentsTexts,
  IF(q.q_acceptedAnswerId, 'yes', 'no') AS Successful

  INTO OUTFILE '/tmp/raw_questions.csv'
  FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"'
  ESCAPED BY '\\'
  LINES TERMINATED BY '\n'

FROM questions_mv AS q
LEFT OUTER JOIN Votes AS v ON q.q_acceptedAnswerId = v.PostId AND v.VoteTypeId = 1
LEFT OUTER JOIN userscommentsquestions_mv AS ucq ON q.q_postID = ucq.q_Id
##WHERE q.q_postID = 651
WHERE ( q.q_acceptedAnswerId IS NULL OR ucq.q_Id IS NULL OR ucq.c_ts_creationDate < v.CreationDate)
GROUP BY QuestionId
LIMIT 5

## Some Example Ids ##################
# 17 - body contains character '"'   #
# 16 - no comments to the question   #
# 19 - question with comments        #
# 3088 - no accepted answer          #
######################################

EOF
