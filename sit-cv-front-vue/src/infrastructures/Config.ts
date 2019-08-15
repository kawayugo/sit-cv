class Config {

  private static INCETANCE : Config;

  public static get instance() {
    if (!this.INCETANCE) {
      this.INCETANCE = new Config();
    }    
    return this.INCETANCE;
  }

  public get isServerMode() {
    return this.endpoint.startsWith('http');
  }
  
  public get endpoint() : string{
    return  process.env.VUE_APP_ENDPOINT || `${location.protocol}//${location.host}`
  }
}

export default Config.instance;