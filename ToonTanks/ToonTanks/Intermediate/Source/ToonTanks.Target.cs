using UnrealBuildTool;

public class ToonTanksTarget : TargetRules
{
	public ToonTanksTarget(TargetInfo Target) : base(Target)
	{
		DefaultBuildSettings = BuildSettingsVersion.V3;
		IncludeOrderVersion = EngineIncludeOrderVersion.Latest;
		Type = TargetType.Game;
		ExtraModuleNames.Add("ToonTanks");
	}
}
