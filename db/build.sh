# Create DB
export PATH=/Applications/Postgres.app/Contents/Versions/9.3/bin:$PATH
psql <<EOF
DROP DATABASE IF EXISTS locator;
CREATE DATABASE locator;
\c locator
CREATE EXTENSION postgis;
CREATE SCHEMA tiger;
CREATE SCHEMA areas;
EOF

# Get the geography
#wget ftp://ftp2.census.gov/geo/tiger/TIGER2014/STATE/tl_2014_us_state.zip
#wget ftp://ftp2.census.gov/geo/tiger/TIGER2014/COUNTY/tl_2014_us_county.zip

# Unzip
unzip data/tl_2014_us_state.zip -d shapefiles
unzip data/tl_2014_us_county.zip -d shapefiles

shp2pgsql -s 4269:4326 -g geom -W "latin1" -I shapefiles/tl_2014_us_state.shp tiger.states | psql -d locator
shp2pgsql -s 4269:4326 -g geom -W "latin1" -I shapefiles/tl_2014_us_county.shp tiger.counties | psql -d locator

psql <<EOF
\c locator
CREATE TABLE areas.monitored (
  tiger VARCHAR(20),
  abbrev CHAR(2),
  name VARCHAR(100),
  geom geometry(MultiPolygon, 4326));
INSERT INTO areas.monitored (tiger, abbrev, name, geom)
  SELECT s.geoid, s.stusps, s.name, s.geom
  FROM tiger.states AS s;
INSERT INTO areas.monitored (tiger, name, geom)
  SELECT c.geoid, c.name, c.geom
  FROM tiger.counties AS c;
CREATE INDEX idx_monitored_geom ON areas.monitored USING gist(geom);
EOF