CREATE TABLE IF NOT EXISTS chart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    amount_in_cents BIGINT,
    status VARCHAR(20)
);

CREATE INDEX idx_chart_uuid ON chart (uuid);

CREATE TABLE IF NOT EXISTS item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36),
    description VARCHAR(100),
    amount BIGINT,
    quantity_available BIGINT,
    enabled boolean
    );

CREATE INDEX idx_item_uuid ON item (uuid);

CREATE TABLE IF NOT EXISTS chart_items (
   chart_id BIGINT,
   item_id BIGINT,
   quantity int,
   item_value BIGINT,

   CONSTRAINT chart_id FOREIGN KEY (chart_id) REFERENCES chart(id),
    CONSTRAINT item_id FOREIGN KEY (item_id) REFERENCES item(id)
);
