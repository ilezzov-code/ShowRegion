CREATE TABLE IF NOT EXISTS players (
    uuid CHAR(36) PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    enable_showing BOOLEAN NOT NULL,
    enable_action_bar BOOLEAN NOT NULL,
    enable_boss_bar BOOLEAN NOT NULL
);
