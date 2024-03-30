using UnrealBuildTool;

public class ToonTanksEditorTarget : TargetRules
{
	public ToonTanksEditorTarget(TargetInfo Target) : base(Target)
	{
		DefaultBuildSettings = BuildSettingsVersion.V3;
		IncludeOrderVersion = EngineIncludeOrderVersion.Latest;
		Type = TargetType.Editor;
		ExtraModuleNames.Add("ToonTanks");
	}
}
