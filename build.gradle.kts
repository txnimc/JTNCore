import toni.blahaj.setup.modImplementation

plugins {
	id("toni.blahaj")
}

blahaj {
	config { }
	setup {
		txnilib("1.0.23")
		forgeConfig()
		conditionalMixin()

		deps.modImplementation(modrinth("azurelib", "pPvo3yDt"))

		deps.modImplementation(modrinth("sodium", "mc1.21.1-0.6.13-fabric"))
		deps.modRuntimeOnly(modrinth("iris", "1.8.8+1.21.1-fabric"))
		deps.runtimeOnly("org.anarres:jcpp:1.4.14") // required for iris
		deps.runtimeOnly("org.antlr:antlr4-runtime:4.13.1") // required for iris
		deps.runtimeOnly("io.github.douira:glsl-transformer:2.0.1") // required for iris

		// World Gen
		deps.modImplementation(modrinth("tectonic", "3.0.0+beta4"))
		deps.modImplementation(modrinth("lithostitched", "1.4.5-fabric-1.21"))
		//deps.modImplementation(modrinth("wwoo", "2.3.4"))
		//deps.modImplementation(modrinth("terralith", "2.5.8"))



		//deps.modImplementation(modrinth("expanded-ecosphere", "XZsvA8Md"))
//		deps.modImplementation(modrinth("cristel-lib", "1.2.8-fabric")) // required for EE
//		deps.runtimeOnly("blue.endless:jankson:1.2.3") // embedded in cristel lib

		//deps.modImplementation(modrinth("terrablender", "XNtIBXyQ")) // required for EE and Terralith compat
		//deps.runtimeOnly("com.electronwill.night-config:toml:3.6.7") // embedded in terrablender
		//deps.runtimeOnly("com.electronwill.night-config:core:3.6.7") // embedded in terrablender

		deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.3.2-beta.4")!!)!!)

		// Immersive Tooltips
		deps.modImplementation("toni.immersivemessages:${mod.loader}-${mod.mcVersion}:1.0.18")
		deps.modImplementation(modrinth("caxton", "0.6.0-alpha.1+1.21-FABRIC"))
		deps.modImplementation("com.github.Chocohead:Fabric-ASM:v2.3")
		deps.implementation("com.github.ben-manes.caffeine:caffeine:3.1.2")

		// utility
		deps.modImplementation(modrinth("world-preview", "qc0AtV3T"))
		//deps.modImplementation(modrinth("chunky", "dPliWter"))
		//deps.modImplementation(modrinth("faster-random", "5.1.0"))
		//deps.modImplementation(modrinth("noisium", "2.3.0+mc1.21-1.21.1"))
		deps.modImplementation(modrinth("xaeros-world-map", "1.39.0_Fabric_1.21"))
		//deps.modRuntimeOnly(modrinth("cyanide", "5.0.0"))
	}
}

repositories {
	maven("https://maven.bawnorton.com/releases")
}