export PATH=/Applications/Postgres.app/Contents/Versions/9.3/bin:$PATH
psql <<EOF
\c locator

WITH
device AS (SELECT ST_SetSRID(ST_MakePoint(-122.3985221, 37.7908101),4326) AS location)
SELECT
  tiger,
  abbrev,
  name
FROM areas.monitored, device
WHERE ST_Intersects(device.location, monitored.geom);

WITH
device AS (SELECT ST_SetSRID(ST_MakePoint(-122.3985221, 37.7908101),4326) AS location),
rough AS (
  SELECT
    geom
  FROM areas.monitored, device
  WHERE NOT ST_Intersects(device.location, geom)
  ORDER BY geom <#> device.location
  LIMIT 10
)
SELECT
  ST_X(device.location) latitude,
  ST_Y(device.location) longitude,
  ST_Distance(device.location, geom, false) AS distance
FROM device, rough
ORDER BY distance
LIMIT 1;

CREATE SCHEMA test;

CREATE TABLE test.points (
  label varchar(30),
  location geometry(point, 4326)
);

CREATE TABLE test.restaurants_staging (
  franchise text,
  lat double precision,
  lon double precision
);
\copy test.restaurants_staging FROM 'data/restaurants.csv' DELIMITER as ',';

INSERT INTO test.points (label, location)
SELECT
  franchise,
  ST_SetSRID(ST_Point(lon,lat),4326)
FROM test.restaurants_staging;

CREATE INDEX idx_test_points ON test.points USING gist(location);

SELECT
  p.label,
  a.tiger,
  a.abbrev,
  a.name,
  ST_X(p.location) latitude,
  ST_Y(p.location) longitude
FROM areas.monitored as a, test.points as p
WHERE ST_Intersects(p.location, a.geom)
LIMIT 20;

EOF