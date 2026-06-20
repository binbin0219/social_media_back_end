-- Add shared_post_id column to support repost/share feature
ALTER TABLE public.posts
ADD COLUMN shared_post_id bigint;

-- Add foreign key constraint (self reference to posts table)
ALTER TABLE public.posts
ADD CONSTRAINT fk_posts_shared_post
FOREIGN KEY (shared_post_id)
REFERENCES public.posts (id)
ON UPDATE NO ACTION
ON DELETE SET NULL;

-- Optional: index for faster feed queries (recommended)
CREATE INDEX IF NOT EXISTS idx_posts_shared_post_id
ON public.posts (shared_post_id);