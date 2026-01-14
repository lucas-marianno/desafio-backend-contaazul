CREATE TABLE IF NOT EXISTS bank_slip (
  id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
  due_date DATE NOT NULL,
  payment_date DATE,
  total_in_cents BIGINT NOT NULL,
  fine BIGINT,
  customer VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PAID', 'CANCELED'))
);
