$compress = @{
  LiteralPath="GpxParse.properties","target\gpxparse-jar-with-dependencies.jar","lancia.cmd","readme_it.md"
  CompressionLevel="Optimal"
  DestinationPath="GpxParse.zip"
}

Set-Location (Split-Path $PSCommandPath)
Set-Location ".."

New-Item -ItemType Directory -Force -Path "xzip"
Copy-Item .\GpxParse.properties -Destination "xzip\GpxParse.properties" -Force
Copy-Item .\target\gpxparse-1.0.jar -Destination "xzip\gpxparse.jar" -Force
Copy-Item .\lancia.cmd -Destination "xzip\lancia.cmd"
Copy-Item .\dati\settaEnvJava.ps1 -Destination "xzip\settaEnvJava.ps1"
Copy-Item .\readme_it.md -Destination "xzip\readme_it.md"

if ( (Test-Path "GpxParse.zip" )) {
  Remove-Item -Path "GpxParse.zip" -Force
}

Compress-Archive -Path "xzip\*.*" -DestinationPath "GpxParse.zip" -CompressionLevel Optimal

Remove-Item -Path "xzip" -Recurse -Force

