#!/usr/bin/env bash
# local-couchbase-init.sh
# Creates a Couchbase bucket on localhost

# --- Configuration ---
COUCHBASE_HOST="localhost"
COUCHBASE_PORT="8091"
USERNAME="username"
PASSWORD="password"

BUCKET_NAME="shortly-urls"
RAM_QUOTA_MB=100
BUCKET_TYPE="couchbase"       # couchbase | ephemeral | memcached
REPLICA_NUMBER=1
EVICTION_POLICY="valueOnly"   # valueOnly | fullEviction
FLUSH_ENABLED=1               # 1=enable flush; 0=disable
DURABILITY_MIN_LEVEL="none"   # none | majority | persistToMajority

# --- Create bucket ---
echo "Creating bucket '${BUCKET_NAME}' on ${COUCHBASE_HOST}:${COUCHBASE_PORT}..."

curl -u "${USERNAME}:${PASSWORD}" -X POST \
  http://${COUCHBASE_HOST}:${COUCHBASE_PORT}/pools/default/buckets \
  -d name="${BUCKET_NAME}" \
  -d ramQuotaMB="${RAM_QUOTA_MB}" \
  -d bucketType="${BUCKET_TYPE}" \
  -d replicaNumber="${REPLICA_NUMBER}" \
  -d evictionPolicy="${EVICTION_POLICY}" \
  -d flushEnabled="${FLUSH_ENABLED}" \
  -d durabilityMinLevel="${DURABILITY_MIN_LEVEL}"

echo
echo "Bucket creation request sent."

# --- Verify ---
echo "Verifying bucket..."
curl -u "${USERNAME}:${PASSWORD}" \
  http://${COUCHBASE_HOST}:${COUCHBASE_PORT}/pools/default/buckets/${BUCKET_NAME} \
  | grep -E '"name"|"bucketType"|"ramQuotaMB"' || echo "Bucket not found!"
