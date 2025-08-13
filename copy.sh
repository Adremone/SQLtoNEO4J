SRC_DIR="./output"
POD_NAME="doc-uiv-inst2-uiv-neo4j-cneo-core-0"
NAMESPACE="doc-uiv-inst2"
TARGET_DIR="/var/lib/neo4j/import"

for file in "$SRC_DIR"/*; do
  [ -f "$file" ] || continue  # skip if not a file
  BASENAME=$(basename "$file")
  echo "Copying $file to $POD_NAME:$TARGET_DIR/$BASENAME"
  kubectl cp "$file" "$NAMESPACE/$POD_NAME:$TARGET_DIR/$BASENAME"
done