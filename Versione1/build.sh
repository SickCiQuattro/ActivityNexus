#!/bin/bash
# Script di compilazione e creazione JAR per ActivityNexus
# Include automaticamente la libreria TreeNode

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   ActivityNexus - Build Script con TreeNode      ║${NC}"
echo -e "${BLUE}║              e JUnit 5 Testing                   ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

# Ottieni la directory dello script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

# Definizione percorsi assoluti
TREE_LIB="$PROJECT_ROOT/tree/tree/build/libs"
TREE_CLASSES="$PROJECT_ROOT/tree/tree/build/classes/java/main"
SRC_DIR="$SCRIPT_DIR/src"
TEST_DIR="$SCRIPT_DIR/src/test/java"
BIN_DIR="$SCRIPT_DIR/bin"
TEST_BIN_DIR="$SCRIPT_DIR/bin/test"
TEMP_LIB_DIR="$SCRIPT_DIR/temp_lib"
LIB_DIR="$SCRIPT_DIR/lib"
JAR_NAME="$SCRIPT_DIR/ActivityNexus_Parte_B.jar"

echo -e "${YELLOW}📁 Directory script: $SCRIPT_DIR${NC}"
echo -e "${YELLOW}📁 Radice progetto: $PROJECT_ROOT${NC}"
echo ""

# Funzione per pulire file temporanei
cleanup() {
    if [ -d "$TEMP_LIB_DIR" ]; then
        echo -e "${YELLOW}🧹 Pulizia file temporanei...${NC}"
        rm -rf "$TEMP_LIB_DIR"
    fi
}

# Trap per pulizia in caso di interruzione
trap cleanup EXIT

# Step 1: Verifica presenza libreria tree e JUnit
echo -e "${BLUE}[1/8]${NC} Verifica dipendenze..."
if [ ! -d "$TREE_LIB" ] || [ ! -d "$TREE_CLASSES" ]; then
    echo -e "${RED}❌ Errore: Libreria tree non trovata!${NC}"
    echo -e "${YELLOW}   Percorso atteso: $TREE_LIB${NC}"
    exit 1
fi

# Verifica JUnit
JUNIT_JARS="$LIB_DIR/junit-jupiter-api-5.10.2.jar:$LIB_DIR/junit-jupiter-engine-5.10.2.jar:$LIB_DIR/junit-platform-launcher-1.10.2.jar:$LIB_DIR/junit-platform-engine-1.10.2.jar:$LIB_DIR/opentest4j-1.3.0.jar:$LIB_DIR/apiguardian-api-1.1.2.jar"
JUNIT_CONSOLE="$LIB_DIR/junit-platform-console-standalone-1.10.2.jar"
if [ ! -f "$LIB_DIR/junit-jupiter-api-5.10.2.jar" ] || [ ! -f "$JUNIT_CONSOLE" ]; then
    echo -e "${RED}❌ Errore: JUnit non trovato! Esegui: cd lib && curl -O https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.2/junit-jupiter-api-5.10.2.jar && ...${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Libreria TreeNode trovata${NC}"
echo -e "${GREEN}✓ JUnit 5 trovato${NC}"
echo ""

# Step 2: Pulizia directory bin
echo -e "${BLUE}[2/8]${NC} Pulizia directory bin..."
if [ -d "$BIN_DIR" ]; then
    rm -rf "$BIN_DIR"
fi
mkdir -p "$BIN_DIR"
mkdir -p "$TEST_BIN_DIR"
echo -e "${GREEN}✓ Directory bin pulita${NC}"
echo ""

# Step 3: Compilazione sorgenti principali
echo -e "${BLUE}[3/8]${NC} Compilazione sorgenti Java principali..."
javac -cp ".:$TREE_LIB/*:$TREE_CLASSES:$JUNIT_JARS" -d "$BIN_DIR" \
    "$SRC_DIR/Main.java" \
    "$SRC_DIR/controller/"*.java \
    "$SRC_DIR/model/"*.java \
    "$SRC_DIR/model/observers/"*.java \
    "$SRC_DIR/model/factory/"*.java \
    "$SRC_DIR/model/state/"*.java \
    "$SRC_DIR/constants/"*.java \
    "$SRC_DIR/util/"*.java \
    "$SRC_DIR/view/"*.java 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la compilazione principale!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Compilazione principale completata${NC}"
echo ""

# Step 4: Compilazione test
echo -e "${BLUE}[4/8]${NC} Compilazione test JUnit..."
# Prima compiliamo MockView dalla directory test/java/view
javac -cp "$BIN_DIR:$TREE_LIB/*:$TREE_CLASSES:$JUNIT_JARS" -d "$TEST_BIN_DIR" \
    "$TEST_DIR/view/"*.java 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la compilazione delle classi di supporto test!${NC}"
    exit 1
fi

# Poi compiliamo i test nella directory test/java
javac -cp "$BIN_DIR:$TEST_BIN_DIR:$TREE_LIB/*:$TREE_CLASSES:$JUNIT_JARS" -d "$TEST_BIN_DIR" \
    "$TEST_DIR/controller/"*.java 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la compilazione test!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Compilazione test completata${NC}"
echo ""

# Step 5: Estrazione libreria TreeNode
echo -e "${BLUE}[5/8]${NC} Estrazione libreria TreeNode..."
mkdir -p "$TEMP_LIB_DIR"
cd "$TEMP_LIB_DIR"
jar xf "$TREE_LIB"/*.jar 2>/dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante l'estrazione della libreria tree!${NC}"
    cd "$SCRIPT_DIR"
    exit 1
fi
cd "$SCRIPT_DIR"
echo -e "${GREEN}✓ Libreria TreeNode estratta${NC}"
echo ""

# Step 6: Copia classi tree in bin
echo -e "${BLUE}[6/8]${NC} Inclusione TreeNode nel JAR..."
if [ -d "$TEMP_LIB_DIR/com" ]; then
    cp -r "$TEMP_LIB_DIR/com" "$BIN_DIR/"
    echo -e "${GREEN}✓ Classi TreeNode copiate in bin/${NC}"
else
    echo -e "${RED}❌ Errore: Classi tree non trovate!${NC}"
    exit 1
fi
echo ""

# Step 7: Esecuzione test JUnit
echo -e "${BLUE}[7/8]${NC} Esecuzione test JUnit..."
java -cp "$BIN_DIR:$TEST_BIN_DIR:$TREE_LIB/*:$TREE_CLASSES:$JUNIT_CONSOLE" \
    org.junit.platform.console.ConsoleLauncher \
    --scan-classpath="$TEST_BIN_DIR" \
    --reports-dir="$SCRIPT_DIR/test-reports" 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Alcuni test sono falliti!${NC}"
    echo -e "${YELLOW}Controlla i report in test-reports/${NC}"
    # Non uscire, continua con la creazione del JAR
else
    echo -e "${GREEN}✓ Tutti i test passati${NC}"
fi
echo ""

# Step 8: Creazione JAR
echo -e "${BLUE}[8/8]${NC} Creazione file JAR..."
jar cvfe "$JAR_NAME" Main -C "$BIN_DIR" . > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la creazione del JAR!${NC}"
    exit 1
fi

# Verifica che TreeNode sia incluso
echo -e "${YELLOW}Verifica inclusione TreeNode...${NC}"
TREE_FILES=$(jar tf "$JAR_NAME" | grep -c "com/scalified/tree")
if [ "$TREE_FILES" -gt 0 ]; then
    echo -e "${GREEN}✓ TreeNode incluso correttamente ($TREE_FILES files)${NC}"
else
    echo -e "${RED}❌ TreeNode non incluso nel JAR!${NC}"
    exit 1
fi
echo ""

# Informazioni finali
JAR_SIZE=$(ls -lh "$JAR_NAME" | awk '{print $5}')
TOTAL_FILES=$(jar tf "$JAR_NAME" | wc -l | tr -d ' ')

echo -e "${GREEN}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║              BUILD COMPLETATO!                    ║${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "📦 ${BLUE}JAR creato:${NC} $JAR_NAME"
echo -e "📏 ${BLUE}Dimensione:${NC} $JAR_SIZE"
echo -e "📄 ${BLUE}Files inclusi:${NC} $TOTAL_FILES"
echo -e "🌲 ${BLUE}TreeNode:${NC} Incluso ($TREE_FILES classi)"
echo ""
echo -e "${YELLOW}Per eseguire l'applicazione:${NC}"
echo -e "  ${GREEN}java -jar $JAR_NAME${NC}"
echo ""
