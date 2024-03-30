using UnrealBuildTool;

public class ToonTanksServerTarget : TargetRules
{
	public ToonTanksServerTarget(TargetInfo Target) : base(Target)
	{
		DefaultBuildSettings = BuildSettingsVersion.V3;
		IncludeOrderVersion = EngineIncludeOrderVersion.Latest;
		Type = TargetType.Server;
		ExtraModuleNames.Add("ToonTanks");
	}
}
