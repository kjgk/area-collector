CREATE TABLE "public"."region" (
  "id" int8 NOT NULL,
  "parent_id" int8,
  "name" varchar(255),
  "code" varchar(255),
  "line" varchar(255),
  "status" int8,
  "level" int8,
  CONSTRAINT "region_pkey" PRIMARY KEY ("id"),
);