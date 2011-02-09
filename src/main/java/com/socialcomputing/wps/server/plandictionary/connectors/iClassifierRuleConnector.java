package com.socialcomputing.wps.server.plandictionary.connectors;

public interface iClassifierRuleConnector extends Iterable<String>
{
   public abstract  String getName(  );
   public abstract  String getDescription(  );
   @Override
   public abstract iEnumerator<String> iterator();
}
