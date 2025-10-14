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
echo -e "${BLUE}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

# Ottieni la directory dello script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

# Definizione percorsi assoluti
TREE_LIB="$PROJECT_ROOT/tree/tree/build/libs"
TREE_CLASSES="$PROJECT_ROOT/tree/tree/build/classes/java/main"
SRC_DIR="$SCRIPT_DIR/src"
BIN_DIR="$SCRIPT_DIR/bin"
TEMP_LIB_DIR="$SCRIPT_DIR/temp_lib"
JAR_NAME="$SCRIPT_DIR/ActivityNexusV4.jar"

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

# Step 1: Verifica presenza libreria tree
echo -e "${BLUE}[1/6]${NC} Verifica libreria TreeNode..."
if [ ! -d "$TREE_LIB" ] || [ ! -d "$TREE_CLASSES" ]; then
    echo -e "${RED}❌ Errore: Libreria tree non trovata!${NC}"
    echo -e "${YELLOW}   Percorso atteso: $TREE_LIB${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Libreria TreeNode trovata${NC}"
echo ""

# Step 2: Pulizia directory bin
echo -e "${BLUE}[2/6]${NC} Pulizia directory bin..."
if [ -d "$BIN_DIR" ]; then
    rm -rf "$BIN_DIR"
fi
mkdir -p "$BIN_DIR"
echo -e "${GREEN}✓ Directory bin pulita${NC}"
echo ""

# Step 3: Compilazione sorgenti
echo -e "${BLUE}[3/6]${NC} Compilazione sorgenti Java..."
javac -cp ".:$TREE_LIB/*:$TREE_CLASSES" -d "$BIN_DIR" \
    "$SRC_DIR/Main.java" \
    "$SRC_DIR/controller/"*.java \
    "$SRC_DIR/model/"*.java \
    "$SRC_DIR/constants/"*.java \
    "$SRC_DIR/util/"*.java \
    "$SRC_DIR/view/"*.java 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la compilazione!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Compilazione completata${NC}"
echo ""

# Step 4: Estrazione libreria TreeNode
echo -e "${BLUE}[4/6]${NC} Estrazione libreria TreeNode..."
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

# Step 5: Copia classi tree in bin
echo -e "${BLUE}[5/6]${NC} Inclusione TreeNode nel JAR..."
if [ -d "$TEMP_LIB_DIR/com" ]; then
    cp -r "$TEMP_LIB_DIR/com" "$BIN_DIR/"
    echo -e "${GREEN}✓ Classi TreeNode copiate in bin/${NC}"
else
    echo -e "${RED}❌ Errore: Classi tree non trovate!${NC}"
    exit 1
fi
echo ""

# Step 6: Creazione JAR
echo -e "${BLUE}[6/6]${NC} Creazione file JAR..."
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
