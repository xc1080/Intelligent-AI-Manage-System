USE `iims`;

-- Local Ollama models for development.
-- Chat model: qwen2.5:0.5b
-- Embedding model: nomic-embed-text

INSERT INTO `iims_ai_chat_models`
(`id`, `rename`, `name`, `key`, `token`, `type`, `url`, `description`, `model_type`, `is_online`, `is_deleted`, `is_deletable`, `detection_time`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES
(10001, 'Local Chat Model', 'qwen2.5:0.5b', NULL, 4096, 2, 'http://localhost:11434', 'Ollama local chat model for development', 1, 1, 0, 1, NOW(), 17, NOW(), 17, NOW()),
(10002, 'Local Embedding Model', 'nomic-embed-text', NULL, 2048, 2, 'http://localhost:11434', 'Ollama local embedding model for RAG', 0, 1, 0, 1, NOW(), 17, NOW(), 17, NOW())
ON DUPLICATE KEY UPDATE
  `rename` = VALUES(`rename`),
  `token` = VALUES(`token`),
  `type` = VALUES(`type`),
  `url` = VALUES(`url`),
  `description` = VALUES(`description`),
  `model_type` = VALUES(`model_type`),
  `is_online` = VALUES(`is_online`),
  `is_deleted` = VALUES(`is_deleted`),
  `update_by` = VALUES(`update_by`),
  `update_time` = NOW();

INSERT INTO `iims_ai_chat_settings`
(`user_id`, `language_model`, `vision_model`, `multimodal_model`, `embedding_model`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES
(17, 10001, NULL, NULL, 10002, 17, NOW(), 17, NOW()),
(18, 10001, NULL, NULL, 10002, 17, NOW(), 17, NOW()),
(21, 10001, NULL, NULL, 10002, 17, NOW(), 17, NOW())
ON DUPLICATE KEY UPDATE
  `language_model` = VALUES(`language_model`),
  `embedding_model` = VALUES(`embedding_model`),
  `update_by` = VALUES(`update_by`),
  `update_time` = NOW();
