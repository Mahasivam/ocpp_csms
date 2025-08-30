-- Add missing id_tag column to id_tags table

ALTER TABLE id_tags 
ADD COLUMN IF NOT EXISTS id_tag VARCHAR(255);