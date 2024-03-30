using UnrealBuildTool;

public class ToonTanksClientTarget : TargetRules
{
	public ToonTanksClientTarget(TargetInfo Target) : base(Target)
	{
		DefaultBuildSettings = BuildSettingsVersion.V3;
		IncludeOrderVersion = EngineIncludeOrderVersion.Latest;
		Type = TargetType.Client;
		ExtraModuleNames.Add("ToonTanks");
	}
}
