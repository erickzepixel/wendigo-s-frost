#!/usr/bin/env python3
"""Restore the exact original GLB from its compact lossless source archive.
Requires Python 3 standard library only. No remeshing or resampling happens.
"""
from pathlib import Path
import base64
import hashlib
import lzma

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "assets-source/wendigo_glacial_exact.glb.xz.b64"
OUTPUT = ROOT / "src/main/resources/assets/wendigosfrost/models/gltf/wendigo_glacial_exact.glb"
EXPECTED_SHA256 = "e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1"
raw = lzma.decompress(base64.b64decode(SOURCE.read_text(encoding="ascii")))
assert hashlib.sha256(raw).hexdigest() == EXPECTED_SHA256, "Original mesh differs from reference"
assert raw[:4] == b"glTF", "GLB header invalid"
OUTPUT.parent.mkdir(parents=True, exist_ok=True)
if not OUTPUT.exists() or OUTPUT.read_bytes() != raw:
    OUTPUT.write_bytes(raw)
print(f"Restored byte-identical GLB: {len(raw)} bytes, SHA-256 verified")
