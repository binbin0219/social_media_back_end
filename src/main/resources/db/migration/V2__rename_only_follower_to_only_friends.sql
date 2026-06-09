-- Update existing data first
UPDATE posts
SET comment_status = 'ONLY_FRIENDS'
WHERE comment_status = 'ONLY_FOLLOWER';

-- Drop old constraint
ALTER TABLE posts
DROP CONSTRAINT IF EXISTS posts_comment_status_check;

-- Recreate constraint with new enum value
ALTER TABLE posts
ADD CONSTRAINT posts_comment_status_check
CHECK (
    comment_status::text = ANY (
        ARRAY[
            'OPEN'::character varying,
            'CLOSED'::character varying,
            'ONLY_FRIENDS'::character varying
        ]::text[]
    )
);