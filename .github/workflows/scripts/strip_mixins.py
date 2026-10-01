#!/usr/bin/env python3
"""Blanks the common "mixins" array so the client audit only covers the "client" array.

mixin-auditor calls MixinEnvironment.getCurrentEnvironment().audit() and exposes no filter, so
the audit scope is whatever mixin configs are registered in the current environment. The common
array is already covered by runServerMixinAudit, and the audit aborts on the first failure, so
leaving it in would let a common-mixin failure hide every client-mixin failure behind it.
"""
__author__ = 'Liuyue_awa'

import json
import pathlib
import sys

CONFIG = pathlib.Path("src/main/resources/carpet-igny-addition.mixins.json")


def main() -> int:
    if not CONFIG.is_file():
        print(f"error: {CONFIG} not found, run this from the repository root", file=sys.stderr)
        return 1

    data = json.loads(CONFIG.read_text(encoding="utf-8"))
    client = data.get("client") or []
    if not client:
        print(f"error: no client mixins in {CONFIG}, refusing to run an empty audit", file=sys.stderr)
        return 1

    common = len(data.get("mixins") or [])
    data["mixins"] = []
    CONFIG.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")

    print(f"client mixins under audit: {len(client)}, skipped common mixins: {common}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
