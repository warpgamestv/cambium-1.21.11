$srcBase = "c:\Users\WarpGamesHD\Desktop\cambium-1.21.11\src\main\generated"
$tgtBase = "c:\Users\WarpGamesHD\Desktop\cambium-1.21.1\src\main\generated"

Write-Output "Checking differences between 1.21.11 generated resources ($srcBase) and 1.21.1 generated resources ($tgtBase)"

Get-ChildItem -Path $srcBase -Recurse -File | ForEach-Object {
    $rel = $_.FullName.Substring($srcBase.Length + 1)
    $target = Join-Path $tgtBase $rel
    if (Test-Path $target) {
        $srcHash = (Get-FileHash $_.FullName).Hash
        $tgtHash = (Get-FileHash $target).Hash
        if ($srcHash -ne $tgtHash) {
            Write-Output "MODIFIED: $rel"
        }
    }
    else {
        Write-Output "MISSING_IN_121.1: $rel"
    }
}

Get-ChildItem -Path $tgtBase -Recurse -File | ForEach-Object {
    $rel = $_.FullName.Substring($tgtBase.Length + 1)
    $source = Join-Path $srcBase $rel
    if (-not (Test-Path $source)) {
        Write-Output "EXTRA_IN_121.1: $rel"
    }
}
Write-Output "Done."
