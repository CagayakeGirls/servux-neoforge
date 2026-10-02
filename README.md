<center><div align="center">

<img height="100" src="src/main/resources/icon.png" width="100"/>

# ServuxForged for NeoForge

Servux unofficial NeoForge port.

<img alt="neoforge" height="56" src="https://raw.githubusercontent.com/KessokuTeaTime/badges-extra/main/assets/cozy/supported/neoforge_vector.svg">

<a href="https://modrinth.com/mod/servuxforged">
<img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">
</a>
<a href="https://www.curseforge.com/minecraft/mc-mods/servuxforged">
<img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg">
</a>

</div></center>

ServuxForged is a library mod used by Masa's mods NeoForge port. It contains some common code previously
duplicated in most of the mods, such as multi-key capable keybinds, configuration GUIs etc.

[Original Repo Readme](Original-README.md)

## Development

This mod use modrinth maven.

```gradle
repositories {
    maven { url 'https://api.modrinth.com/maven' }
}

dependencies {
    modImplementation "maven.modrinth:servuxforged:${servuxforged_version}"
}
```

or use KessokuTeaTime maven

```gradle
repositories {
    maven { url 'https://maven.kessokuteatime.work/releases' }
}

dependencies {
    modImplementation "team.cagayakegirls.servuxforged:servuxforged:${servuxforged_version}"
}
```

> Note: "${servuxforged_version}" can be found in [Modrinth](https://modrinth.com/mod/servuxforged)

## Compiling
- Clone the repository
- Open a command prompt/terminal to the repository directory
- run 'gradlew build'
- The built jar file will be in build/libs/

## Credits
- [maruohon/servux](https://github.com/maruohon/servux)
- [sakura-ryoko/servux](https://github.com/sakura-ryoko/servux)