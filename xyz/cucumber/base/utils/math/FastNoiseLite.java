package xyz.cucumber.base.utils.math;

public class FastNoiseLite {
   private int mSeed = 1337;
   private float mFrequency = 0.01F;
   private FastNoiseLite.NoiseType mNoiseType;
   private FastNoiseLite.RotationType3D mRotationType3D;
   private FastNoiseLite.TransformType3D mTransformType3D;
   private FastNoiseLite.FractalType mFractalType;
   private int mOctaves;
   private float mLacunarity;
   private float mGain;
   private float mWeightedStrength;
   private float mPingPongStrength;
   private float mFractalBounding;
   private FastNoiseLite.CellularDistanceFunction mCellularDistanceFunction;
   private FastNoiseLite.CellularReturnType mCellularReturnType;
   private float mCellularJitterModifier;
   private FastNoiseLite.DomainWarpType mDomainWarpType;
   private FastNoiseLite.TransformType3D mWarpTransformType3D;
   private float mDomainWarpAmp;
   private static final float[] Gradients2D = new float[]{0.13052619F, 0.9914449F, 0.38268343F, 0.9238795F, 0.6087614F, 0.7933533F, 0.7933533F, 0.6087614F, 0.9238795F, 0.38268343F, 0.9914449F, 0.13052619F, 0.9914449F, -0.13052619F, 0.9238795F, -0.38268343F, 0.7933533F, -0.6087614F, 0.6087614F, -0.7933533F, 0.38268343F, -0.9238795F, 0.13052619F, -0.9914449F, -0.13052619F, -0.9914449F, -0.38268343F, -0.9238795F, -0.6087614F, -0.7933533F, -0.7933533F, -0.6087614F, -0.9238795F, -0.38268343F, -0.9914449F, -0.13052619F, -0.9914449F, 0.13052619F, -0.9238795F, 0.38268343F, -0.7933533F, 0.6087614F, -0.6087614F, 0.7933533F, -0.38268343F, 0.9238795F, -0.13052619F, 0.9914449F, 0.13052619F, 0.9914449F, 0.38268343F, 0.9238795F, 0.6087614F, 0.7933533F, 0.7933533F, 0.6087614F, 0.9238795F, 0.38268343F, 0.9914449F, 0.13052619F, 0.9914449F, -0.13052619F, 0.9238795F, -0.38268343F, 0.7933533F, -0.6087614F, 0.6087614F, -0.7933533F, 0.38268343F, -0.9238795F, 0.13052619F, -0.9914449F, -0.13052619F, -0.9914449F, -0.38268343F, -0.9238795F, -0.6087614F, -0.7933533F, -0.7933533F, -0.6087614F, -0.9238795F, -0.38268343F, -0.9914449F, -0.13052619F, -0.9914449F, 0.13052619F, -0.9238795F, 0.38268343F, -0.7933533F, 0.6087614F, -0.6087614F, 0.7933533F, -0.38268343F, 0.9238795F, -0.13052619F, 0.9914449F, 0.13052619F, 0.9914449F, 0.38268343F, 0.9238795F, 0.6087614F, 0.7933533F, 0.7933533F, 0.6087614F, 0.9238795F, 0.38268343F, 0.9914449F, 0.13052619F, 0.9914449F, -0.13052619F, 0.9238795F, -0.38268343F, 0.7933533F, -0.6087614F, 0.6087614F, -0.7933533F, 0.38268343F, -0.9238795F, 0.13052619F, -0.9914449F, -0.13052619F, -0.9914449F, -0.38268343F, -0.9238795F, -0.6087614F, -0.7933533F, -0.7933533F, -0.6087614F, -0.9238795F, -0.38268343F, -0.9914449F, -0.13052619F, -0.9914449F, 0.13052619F, -0.9238795F, 0.38268343F, -0.7933533F, 0.6087614F, -0.6087614F, 0.7933533F, -0.38268343F, 0.9238795F, -0.13052619F, 0.9914449F, 0.13052619F, 0.9914449F, 0.38268343F, 0.9238795F, 0.6087614F, 0.7933533F, 0.7933533F, 0.6087614F, 0.9238795F, 0.38268343F, 0.9914449F, 0.13052619F, 0.9914449F, -0.13052619F, 0.9238795F, -0.38268343F, 0.7933533F, -0.6087614F, 0.6087614F, -0.7933533F, 0.38268343F, -0.9238795F, 0.13052619F, -0.9914449F, -0.13052619F, -0.9914449F, -0.38268343F, -0.9238795F, -0.6087614F, -0.7933533F, -0.7933533F, -0.6087614F, -0.9238795F, -0.38268343F, -0.9914449F, -0.13052619F, -0.9914449F, 0.13052619F, -0.9238795F, 0.38268343F, -0.7933533F, 0.6087614F, -0.6087614F, 0.7933533F, -0.38268343F, 0.9238795F, -0.13052619F, 0.9914449F, 0.13052619F, 0.9914449F, 0.38268343F, 0.9238795F, 0.6087614F, 0.7933533F, 0.7933533F, 0.6087614F, 0.9238795F, 0.38268343F, 0.9914449F, 0.13052619F, 0.9914449F, -0.13052619F, 0.9238795F, -0.38268343F, 0.7933533F, -0.6087614F, 0.6087614F, -0.7933533F, 0.38268343F, -0.9238795F, 0.13052619F, -0.9914449F, -0.13052619F, -0.9914449F, -0.38268343F, -0.9238795F, -0.6087614F, -0.7933533F, -0.7933533F, -0.6087614F, -0.9238795F, -0.38268343F, -0.9914449F, -0.13052619F, -0.9914449F, 0.13052619F, -0.9238795F, 0.38268343F, -0.7933533F, 0.6087614F, -0.6087614F, 0.7933533F, -0.38268343F, 0.9238795F, -0.13052619F, 0.9914449F, 0.38268343F, 0.9238795F, 0.9238795F, 0.38268343F, 0.9238795F, -0.38268343F, 0.38268343F, -0.9238795F, -0.38268343F, -0.9238795F, -0.9238795F, -0.38268343F, -0.9238795F, 0.38268343F, -0.38268343F, 0.9238795F};
   private static final float[] RandVecs2D = new float[]{-0.2700222F, -0.9628541F, 0.38630927F, -0.9223693F, 0.04444859F, -0.9990117F, -0.59925234F, -0.80056024F, -0.781928F, 0.62336874F, 0.9464672F, 0.32279992F, -0.6514147F, -0.7587219F, 0.93784726F, 0.34704837F, -0.8497876F, -0.52712524F, -0.87904257F, 0.47674325F, -0.8923003F, -0.45144236F, -0.37984443F, -0.9250504F, -0.9951651F, 0.09821638F, 0.7724398F, -0.635088F, 0.75732833F, -0.6530343F, -0.9928005F, -0.119780056F, -0.05326657F, 0.99858034F, 0.97542536F, -0.22033007F, -0.76650184F, 0.64224213F, 0.9916367F, 0.12906061F, -0.99469686F, 0.10285038F, -0.53792053F, -0.8429955F, 0.50228155F, -0.86470413F, 0.45598215F, -0.8899889F, -0.8659131F, -0.50019443F, 0.08794584F, -0.9961253F, -0.5051685F, 0.8630207F, 0.7753185F, -0.6315704F, -0.69219446F, 0.72171104F, -0.51916593F, -0.85467345F, 0.8978623F, -0.4402764F, -0.17067741F, 0.98532695F, -0.935343F, -0.35374206F, -0.99924046F, 0.038967468F, -0.2882064F, -0.9575683F, -0.96638113F, 0.2571138F, -0.87597144F, -0.48236302F, -0.8303123F, -0.55729836F, 0.051101338F, -0.99869347F, -0.85583735F, -0.51724505F, 0.098870255F, 0.9951003F, 0.9189016F, 0.39448678F, -0.24393758F, -0.96979094F, -0.81214094F, -0.5834613F, -0.99104315F, 0.13354214F, 0.8492424F, -0.52800316F, -0.9717839F, -0.23587295F, 0.9949457F, 0.10041421F, 0.6241065F, -0.7813392F, 0.6629103F, 0.74869883F, -0.7197418F, 0.6942418F, -0.8143371F, -0.58039224F, 0.10452105F, -0.9945227F, -0.10659261F, -0.99430275F, 0.44579968F, -0.8951328F, 0.105547406F, 0.99441427F, -0.9927903F, 0.11986445F, -0.83343667F, 0.55261505F, 0.9115562F, -0.4111756F, 0.8285545F, -0.55990845F, 0.7217098F, -0.6921958F, 0.49404928F, -0.8694339F, -0.36523214F, -0.9309165F, -0.9696607F, 0.24445485F, 0.089255095F, -0.9960088F, 0.5354071F, -0.8445941F, -0.10535762F, 0.9944344F, -0.98902845F, 0.1477251F, 0.004856105F, 0.9999882F, 0.98855984F, 0.15082914F, 0.92861295F, -0.37104982F, -0.5832394F, -0.8123003F, 0.30152076F, 0.9534596F, -0.95751107F, 0.28839657F, 0.9715802F, -0.23671055F, 0.2299818F, 0.97319496F, 0.9557638F, -0.2941352F, 0.7409561F, 0.67155343F, -0.9971514F, -0.07542631F, 0.69057107F, -0.7232645F, -0.2907137F, -0.9568101F, 0.5912778F, -0.80646795F, -0.94545925F, -0.3257405F, 0.66644555F, 0.7455537F, 0.6236135F, 0.78173286F, 0.9126994F, -0.40863165F, -0.8191762F, 0.57354194F, -0.8812746F, -0.4726046F, 0.99533135F, 0.09651673F, 0.98556507F, -0.16929697F, -0.8495981F, 0.52743065F, 0.6174854F, -0.78658235F, 0.85081565F, 0.5254643F, 0.99850327F, -0.0546925F, 0.19713716F, -0.98037595F, 0.66078556F, -0.7505747F, -0.030974941F, 0.9995202F, -0.6731661F, 0.73949134F, -0.71950185F, -0.69449055F, 0.97275114F, 0.2318516F, 0.9997059F, -0.02425069F, 0.44217876F, -0.89692694F, 0.9981351F, -0.061043672F, -0.9173661F, -0.39804456F, -0.81500566F, -0.579453F, -0.87893313F, 0.476945F, 0.015860584F, 0.99987423F, -0.8095465F, 0.5870558F, -0.9165899F, -0.39982867F, -0.8023543F, 0.5968481F, -0.5176738F, 0.85557806F, -0.8154407F, -0.57884055F, 0.40220103F, -0.91555136F, -0.9052557F, -0.4248672F, 0.7317446F, 0.681579F, -0.56476325F, -0.825253F, -0.8403276F, -0.54207885F, -0.93142813F, 0.36392525F, 0.52381986F, 0.85182905F, 0.7432804F, -0.66898F, -0.9853716F, -0.17041974F, 0.46014687F, 0.88784283F, 0.8258554F, 0.56388193F, 0.6182366F, 0.785992F, 0.83315027F, -0.55304664F, 0.15003075F, 0.9886813F, -0.6623304F, -0.7492119F, -0.66859865F, 0.74362344F, 0.7025606F, 0.7116239F, -0.54193896F, -0.84041786F, -0.33886164F, 0.9408362F, 0.833153F, 0.55304253F, -0.29897207F, -0.95426184F, 0.2638523F, 0.9645631F, 0.12410874F, -0.9922686F, -0.7282649F, -0.6852957F, 0.69625F, 0.71779937F, -0.91835356F, 0.395761F, -0.6326102F, -0.7744703F, -0.9331892F, -0.35938552F, -0.11537793F, -0.99332166F, 0.9514975F, -0.30765656F, -0.08987977F, -0.9959526F, 0.6678497F, 0.7442962F, 0.79524004F, -0.6062947F, -0.6462007F, -0.7631675F, -0.27335986F, 0.96191186F, 0.966959F, -0.25493184F, -0.9792895F, 0.20246519F, -0.5369503F, -0.84361386F, -0.27003646F, -0.9628501F, -0.6400277F, 0.76835185F, -0.78545374F, -0.6189204F, 0.060059056F, -0.9981948F, -0.024557704F, 0.9996984F, -0.65983623F, 0.7514095F, -0.62538946F, -0.7803128F, -0.6210409F, -0.7837782F, 0.8348889F, 0.55041856F, -0.15922752F, 0.9872419F, 0.83676225F, 0.54756635F, -0.8675754F, -0.4973057F, -0.20226626F, -0.97933054F, 0.939919F, 0.34139755F, 0.98774046F, -0.1561049F, -0.90344554F, 0.42870283F, 0.12698042F, -0.9919052F, -0.3819601F, 0.92417884F, 0.9754626F, 0.22016525F, -0.32040158F, -0.94728184F, -0.9874761F, 0.15776874F, 0.025353484F, -0.99967855F, 0.4835131F, -0.8753371F, -0.28508F, -0.9585037F, -0.06805516F, -0.99768156F, -0.7885244F, -0.61500347F, 0.3185392F, -0.9479097F, 0.8880043F, 0.45983514F, 0.64769214F, -0.76190215F, 0.98202413F, 0.18875542F, 0.93572754F, -0.35272372F, -0.88948953F, 0.45695552F, 0.7922791F, 0.6101588F, 0.74838185F, 0.66326815F, -0.728893F, -0.68462765F, 0.8729033F, -0.48789328F, 0.8288346F, 0.5594937F, 0.08074567F, 0.99673474F, 0.97991484F, -0.1994165F, -0.5807307F, -0.81409574F, -0.47000498F, -0.8826638F, 0.2409493F, 0.9705377F, 0.9437817F, -0.33056942F, -0.89279985F, -0.45045355F, -0.80696225F, 0.59060305F, 0.062589735F, 0.99803936F, -0.93125975F, 0.36435598F, 0.57774496F, 0.81621736F, -0.3360096F, -0.9418586F, 0.69793206F, -0.71616393F, -0.0020081573F, -0.999998F, -0.18272944F, -0.98316324F, -0.6523912F, 0.7578824F, -0.43026268F, -0.9027037F, -0.9985126F, -0.054520912F, -0.010281022F, -0.99994713F, -0.49460712F, 0.86911666F, -0.299935F, 0.95395964F, 0.8165472F, 0.5772787F, 0.26974604F, 0.9629315F, -0.7306287F, -0.68277496F, -0.7590952F, -0.65097964F, -0.9070538F, 0.4210146F, -0.5104861F, -0.859886F, 0.86133504F, 0.5080373F, 0.50078815F, -0.8655699F, -0.6541582F, 0.7563578F, -0.83827555F, -0.54524684F, 0.6940071F, 0.7199682F, 0.06950936F, 0.9975813F, 0.17029423F, -0.9853933F, 0.26959732F, 0.9629731F, 0.55196124F, -0.83386976F, 0.2256575F, -0.9742067F, 0.42152628F, -0.9068162F, 0.48818734F, -0.87273884F, -0.3683855F, -0.92967314F, -0.98253906F, 0.18605645F, 0.81256473F, 0.582871F, 0.3196461F, -0.947537F, 0.9570914F, 0.28978625F, -0.6876655F, -0.7260276F, -0.9988771F, -0.04737673F, -0.1250179F, 0.9921545F, -0.82801336F, 0.56070834F, 0.93248636F, -0.36120513F, 0.63946533F, 0.7688199F, -0.016238471F, -0.99986815F, -0.99550146F, -0.094746135F, -0.8145332F, 0.580117F, 0.4037328F, -0.91487694F, 0.9944263F, 0.10543368F, -0.16247116F, 0.9867133F, -0.9949488F, -0.10038388F, -0.69953024F, 0.714603F, 0.5263415F, -0.85027325F, -0.5395222F, 0.8419714F, 0.65793705F, 0.7530729F, 0.014267588F, -0.9998982F, -0.6734384F, 0.7392433F, 0.6394121F, -0.7688642F, 0.9211571F, 0.38919085F, -0.14663722F, -0.98919034F, -0.7823181F, 0.6228791F, -0.5039611F, -0.8637264F, -0.774312F, -0.632804F};
   private static final float[] Gradients3D = new float[]{0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F, -1.0F, 0.0F};
   private static final float[] RandVecs3D = new float[]{-0.7292737F, -0.66184396F, 0.17355819F, 0.0F, 0.7902921F, -0.5480887F, -0.2739291F, 0.0F, 0.7217579F, 0.62262124F, -0.3023381F, 0.0F, 0.5656831F, -0.8208298F, -0.079000026F, 0.0F, 0.76004905F, -0.55559796F, -0.33709997F, 0.0F, 0.37139457F, 0.50112647F, 0.78162545F, 0.0F, -0.12770624F, -0.4254439F, -0.8959289F, 0.0F, -0.2881561F, -0.5815839F, 0.7607406F, 0.0F, 0.5849561F, -0.6628202F, -0.4674352F, 0.0F, 0.33071712F, 0.039165374F, 0.94291687F, 0.0F, 0.8712122F, -0.41133744F, -0.26793817F, 0.0F, 0.580981F, 0.7021916F, 0.41156778F, 0.0F, 0.5037569F, 0.6330057F, -0.5878204F, 0.0F, 0.44937122F, 0.6013902F, 0.6606023F, 0.0F, -0.6878404F, 0.090188906F, -0.7202372F, 0.0F, -0.59589565F, -0.64693505F, 0.47579765F, 0.0F, -0.5127052F, 0.1946922F, -0.83619875F, 0.0F, -0.99115074F, -0.054102764F, -0.12121531F, 0.0F, -0.21497211F, 0.9720882F, -0.09397608F, 0.0F, -0.7518651F, -0.54280573F, 0.37424695F, 0.0F, 0.5237069F, 0.8516377F, -0.021078179F, 0.0F, 0.6333505F, 0.19261672F, -0.74951047F, 0.0F, -0.06788242F, 0.39983058F, 0.9140719F, 0.0F, -0.55386287F, -0.47298968F, -0.6852129F, 0.0F, -0.72614557F, -0.5911991F, 0.35099334F, 0.0F, -0.9229275F, -0.17828088F, 0.34120494F, 0.0F, -0.6968815F, 0.65112746F, 0.30064803F, 0.0F, 0.96080446F, -0.20983632F, -0.18117249F, 0.0F, 0.068171464F, -0.9743405F, 0.21450691F, 0.0F, -0.3577285F, -0.6697087F, -0.65078455F, 0.0F, -0.18686211F, 0.7648617F, -0.61649746F, 0.0F, -0.65416974F, 0.3967915F, 0.64390874F, 0.0F, 0.699334F, -0.6164538F, 0.36182392F, 0.0F, -0.15466657F, 0.6291284F, 0.7617583F, 0.0F, -0.6841613F, -0.2580482F, -0.68215424F, 0.0F, 0.5383981F, 0.4258655F, 0.727163F, 0.0F, -0.5026988F, -0.7939833F, -0.3418837F, 0.0F, 0.32029718F, 0.28344154F, 0.9039196F, 0.0F, 0.86832273F, -3.7626564E-4F, -0.49599952F, 0.0F, 0.79112005F, -0.085110456F, 0.60571057F, 0.0F, -0.04011016F, -0.43972486F, 0.8972364F, 0.0F, 0.914512F, 0.35793462F, -0.18854876F, 0.0F, -0.96120393F, -0.27564842F, 0.010246669F, 0.0F, 0.65103614F, -0.28777993F, -0.70237786F, 0.0F, -0.20417863F, 0.73652375F, 0.6448596F, 0.0F, -0.7718264F, 0.37906268F, 0.5104856F, 0.0F, -0.30600828F, -0.7692988F, 0.56083715F, 0.0F, 0.45400733F, -0.5024843F, 0.73578995F, 0.0F, 0.48167956F, 0.6021208F, -0.636738F, 0.0F, 0.69619805F, -0.32221973F, 0.6414692F, 0.0F, -0.65321606F, -0.6781149F, 0.33685157F, 0.0F, 0.50893015F, -0.61546624F, -0.60182345F, 0.0F, -0.16359198F, -0.9133605F, -0.37284088F, 0.0F, 0.5240802F, -0.8437664F, 0.11575059F, 0.0F, 0.5902587F, 0.4983818F, -0.63498837F, 0.0F, 0.5863228F, 0.49476475F, 0.6414308F, 0.0F, 0.6779335F, 0.23413453F, 0.6968409F, 0.0F, 0.7177054F, -0.68589795F, 0.12017863F, 0.0F, -0.532882F, -0.5205125F, 0.6671608F, 0.0F, -0.8654874F, -0.07007271F, -0.4960054F, 0.0F, -0.286181F, 0.79520893F, 0.53454953F, 0.0F, -0.048495296F, 0.98108363F, -0.18741156F, 0.0F, -0.63585216F, 0.60583484F, 0.47818002F, 0.0F, 0.62547946F, -0.28616196F, 0.72586966F, 0.0F, -0.258526F, 0.50619495F, -0.8227582F, 0.0F, 0.021363068F, 0.50640166F, -0.862033F, 0.0F, 0.20011178F, 0.85992634F, 0.46955505F, 0.0F, 0.47435614F, 0.6014985F, -0.6427953F, 0.0F, 0.6622994F, -0.52024746F, -0.539168F, 0.0F, 0.08084973F, -0.65327203F, 0.7527941F, 0.0F, -0.6893687F, 0.059286036F, 0.7219805F, 0.0F, -0.11218871F, -0.96731853F, 0.22739525F, 0.0F, 0.7344116F, 0.59796685F, -0.3210533F, 0.0F, 0.5789393F, -0.24888498F, 0.776457F, 0.0F, 0.69881827F, 0.35571697F, -0.6205791F, 0.0F, -0.86368454F, -0.27487713F, -0.4224826F, 0.0F, -0.4247028F, -0.46408808F, 0.77733505F, 0.0F, 0.5257723F, -0.84270173F, 0.11583299F, 0.0F, 0.93438303F, 0.31630248F, -0.16395439F, 0.0F, -0.10168364F, -0.8057303F, -0.58348876F, 0.0F, -0.6529239F, 0.50602126F, -0.5635893F, 0.0F, -0.24652861F, -0.9668206F, -0.06694497F, 0.0F, -0.9776897F, -0.20992506F, -0.0073688254F, 0.0F, 0.7736893F, 0.57342446F, 0.2694238F, 0.0F, -0.6095088F, 0.4995679F, 0.6155737F, 0.0F, 0.5794535F, 0.7434547F, 0.33392924F, 0.0F, -0.8226211F, 0.081425816F, 0.56272936F, 0.0F, -0.51038545F, 0.47036678F, 0.719904F, 0.0F, -0.5764972F, -0.072316565F, -0.81389266F, 0.0F, 0.7250629F, 0.39499715F, -0.56414634F, 0.0F, -0.1525424F, 0.48608407F, -0.8604958F, 0.0F, -0.55509764F, -0.49578208F, 0.6678823F, 0.0F, -0.18836144F, 0.91458696F, 0.35784173F, 0.0F, 0.76255566F, -0.54144084F, -0.35404897F, 0.0F, -0.5870232F, -0.3226498F, -0.7424964F, 0.0F, 0.30511242F, 0.2262544F, -0.9250488F, 0.0F, 0.63795763F, 0.57724243F, -0.50970703F, 0.0F, -0.5966776F, 0.14548524F, -0.7891831F, 0.0F, -0.65833056F, 0.65554875F, -0.36994147F, 0.0F, 0.74348927F, 0.23510846F, 0.6260573F, 0.0F, 0.5562114F, 0.82643604F, -0.08736329F, 0.0F, -0.302894F, -0.8251527F, 0.47684193F, 0.0F, 0.11293438F, -0.9858884F, -0.123571075F, 0.0F, 0.5937653F, -0.5896814F, 0.5474657F, 0.0F, 0.6757964F, -0.58357584F, -0.45026484F, 0.0F, 0.7242303F, -0.11527198F, 0.67985505F, 0.0F, -0.9511914F, 0.0753624F, -0.29925808F, 0.0F, 0.2539471F, -0.18863393F, 0.9486454F, 0.0F, 0.5714336F, -0.16794509F, -0.8032796F, 0.0F, -0.06778235F, 0.39782694F, 0.9149532F, 0.0F, 0.6074973F, 0.73306F, -0.30589226F, 0.0F, -0.54354787F, 0.16758224F, 0.8224791F, 0.0F, -0.5876678F, -0.3380045F, -0.7351187F, 0.0F, -0.79675627F, 0.040978227F, -0.60290986F, 0.0F, -0.19963509F, 0.8706295F, 0.4496111F, 0.0F, -0.027876602F, -0.91062325F, -0.4122962F, 0.0F, -0.7797626F, -0.6257635F, 0.019757755F, 0.0F, -0.5211233F, 0.74016446F, -0.42495546F, 0.0F, 0.8575425F, 0.4053273F, -0.31675017F, 0.0F, 0.10452233F, 0.8390196F, -0.53396744F, 0.0F, 0.3501823F, 0.9242524F, -0.15208502F, 0.0F, 0.19878499F, 0.076476134F, 0.9770547F, 0.0F, 0.78459966F, 0.6066257F, -0.12809642F, 0.0F, 0.09006737F, -0.97509897F, -0.20265691F, 0.0F, -0.82743436F, -0.54229957F, 0.14582036F, 0.0F, -0.34857976F, -0.41580227F, 0.8400004F, 0.0F, -0.2471779F, -0.730482F, -0.6366311F, 0.0F, -0.3700155F, 0.8577948F, 0.35675845F, 0.0F, 0.59133947F, -0.54831195F, -0.59133035F, 0.0F, 0.120487355F, -0.7626472F, -0.6354935F, 0.0F, 0.6169593F, 0.03079648F, 0.7863923F, 0.0F, 0.12581569F, -0.664083F, -0.73699677F, 0.0F, -0.6477565F, -0.17401473F, -0.74170774F, 0.0F, 0.6217889F, -0.7804431F, -0.06547655F, 0.0F, 0.6589943F, -0.6096988F, 0.44044736F, 0.0F, -0.26898375F, -0.6732403F, -0.68876356F, 0.0F, -0.38497752F, 0.56765425F, 0.7277094F, 0.0F, 0.57544446F, 0.81104714F, -0.10519635F, 0.0F, 0.91415936F, 0.3832948F, 0.13190056F, 0.0F, -0.10792532F, 0.9245494F, 0.36545935F, 0.0F, 0.3779771F, 0.30431488F, 0.87437165F, 0.0F, -0.21428852F, -0.8259286F, 0.5214617F, 0.0F, 0.58025444F, 0.41480985F, -0.7008834F, 0.0F, -0.19826609F, 0.85671616F, -0.47615966F, 0.0F, -0.033815537F, 0.37731808F, -0.9254661F, 0.0F, -0.68679225F, -0.6656598F, 0.29191336F, 0.0F, 0.7731743F, -0.28757936F, -0.565243F, 0.0F, -0.09655942F, 0.91937083F, -0.3813575F, 0.0F, 0.27157024F, -0.957791F, -0.09426606F, 0.0F, 0.24510157F, -0.6917999F, -0.6792188F, 0.0F, 0.97770077F, -0.17538553F, 0.115503654F, 0.0F, -0.522474F, 0.8521607F, 0.029036159F, 0.0F, -0.77348804F, -0.52612925F, 0.35341796F, 0.0F, -0.71344924F, -0.26954725F, 0.6467878F, 0.0F, 0.16440372F, 0.5105846F, -0.84396374F, 0.0F, 0.6494636F, 0.055856112F, 0.7583384F, 0.0F, -0.4711971F, 0.50172806F, -0.7254256F, 0.0F, -0.63357645F, -0.23816863F, -0.7361091F, 0.0F, -0.9021533F, -0.2709478F, -0.33571818F, 0.0F, -0.3793711F, 0.8722581F, 0.3086152F, 0.0F, -0.68555987F, -0.32501432F, 0.6514394F, 0.0F, 0.29009423F, -0.7799058F, -0.5546101F, 0.0F, -0.20983194F, 0.8503707F, 0.48253515F, 0.0F, -0.45926037F, 0.6598504F, -0.5947077F, 0.0F, 0.87159455F, 0.09616365F, -0.48070312F, 0.0F, -0.6776666F, 0.71185046F, -0.1844907F, 0.0F, 0.7044378F, 0.3124276F, 0.637304F, 0.0F, -0.7052319F, -0.24010932F, -0.6670798F, 0.0F, 0.081921004F, -0.72073364F, -0.68835455F, 0.0F, -0.6993681F, -0.5875763F, -0.4069869F, 0.0F, -0.12814544F, 0.6419896F, 0.75592864F, 0.0F, -0.6337388F, -0.67854714F, -0.3714147F, 0.0F, 0.5565052F, -0.21688876F, -0.8020357F, 0.0F, -0.57915545F, 0.7244372F, -0.3738579F, 0.0F, 0.11757791F, -0.7096451F, 0.69467926F, 0.0F, -0.613462F, 0.13236311F, 0.7785528F, 0.0F, 0.69846356F, -0.029805163F, -0.7150247F, 0.0F, 0.83180827F, -0.3930172F, 0.39195976F, 0.0F, 0.14695764F, 0.055416517F, -0.98758924F, 0.0F, 0.70886856F, -0.2690504F, 0.65201014F, 0.0F, 0.27260533F, 0.67369765F, -0.68688995F, 0.0F, -0.65912956F, 0.30354586F, -0.68804663F, 0.0F, 0.48151314F, -0.752827F, 0.4487723F, 0.0F, 0.943001F, 0.16756473F, -0.28752613F, 0.0F, 0.43480295F, 0.7695305F, -0.46772778F, 0.0F, 0.39319962F, 0.5944736F, 0.70142365F, 0.0F, 0.72543365F, -0.60392565F, 0.33018148F, 0.0F, 0.75902355F, -0.6506083F, 0.024333132F, 0.0F, -0.8552769F, -0.3430043F, 0.38839358F, 0.0F, -0.6139747F, 0.6981725F, 0.36822575F, 0.0F, -0.74659055F, -0.575201F, 0.33428493F, 0.0F, 0.5730066F, 0.8105555F, -0.12109168F, 0.0F, -0.92258775F, -0.3475211F, -0.16751404F, 0.0F, -0.71058166F, -0.47196922F, -0.5218417F, 0.0F, -0.0856461F, 0.35830015F, 0.9296697F, 0.0F, -0.8279698F, -0.2043157F, 0.5222271F, 0.0F, 0.42794403F, 0.278166F, 0.8599346F, 0.0F, 0.539908F, -0.78571206F, -0.3019204F, 0.0F, 0.5678404F, -0.5495414F, -0.61283076F, 0.0F, -0.9896071F, 0.13656391F, -0.045034185F, 0.0F, -0.6154343F, -0.64408755F, 0.45430374F, 0.0F, 0.10742044F, -0.79463404F, 0.59750944F, 0.0F, -0.359545F, -0.888553F, 0.28495783F, 0.0F, -0.21804053F, 0.1529889F, 0.9638738F, 0.0F, -0.7277432F, -0.61640507F, -0.30072346F, 0.0F, 0.7249729F, -0.0066971947F, 0.68874484F, 0.0F, -0.5553659F, -0.5336586F, 0.6377908F, 0.0F, 0.5137558F, 0.79762083F, -0.316F, 0.0F, -0.3794025F, 0.92456084F, -0.035227515F, 0.0F, 0.82292485F, 0.27453658F, -0.49741766F, 0.0F, -0.5404114F, 0.60911417F, 0.5804614F, 0.0F, 0.8036582F, -0.27030295F, 0.5301602F, 0.0F, 0.60443187F, 0.68329686F, 0.40959433F, 0.0F, 0.06389989F, 0.96582085F, -0.2512108F, 0.0F, 0.10871133F, 0.74024713F, -0.6634878F, 0.0F, -0.7134277F, -0.6926784F, 0.10591285F, 0.0F, 0.64588976F, -0.57245487F, -0.50509584F, 0.0F, -0.6553931F, 0.73814714F, 0.15999562F, 0.0F, 0.39109614F, 0.91888714F, -0.05186756F, 0.0F, -0.48790225F, -0.5904377F, 0.64291114F, 0.0F, 0.601479F, 0.77074414F, -0.21018201F, 0.0F, -0.5677173F, 0.7511361F, 0.33688518F, 0.0F, 0.7858574F, 0.22667466F, 0.5753667F, 0.0F, -0.45203456F, -0.6042227F, -0.65618575F, 0.0F, 0.0022721163F, 0.4132844F, -0.9105992F, 0.0F, -0.58157516F, -0.5162926F, 0.6286591F, 0.0F, -0.03703705F, 0.8273786F, 0.5604221F, 0.0F, -0.51196927F, 0.79535437F, -0.324498F, 0.0F, -0.26824173F, -0.957229F, -0.10843876F, 0.0F, -0.23224828F, -0.9679131F, -0.09594243F, 0.0F, 0.3554329F, -0.8881506F, 0.29130062F, 0.0F, 0.73465204F, -0.4371373F, 0.5188423F, 0.0F, 0.998512F, 0.046590112F, -0.028339446F, 0.0F, -0.37276876F, -0.9082481F, 0.19007573F, 0.0F, 0.9173738F, -0.3483642F, 0.19252984F, 0.0F, 0.2714911F, 0.41475296F, -0.86848867F, 0.0F, 0.5131763F, -0.71163344F, 0.4798207F, 0.0F, -0.87373537F, 0.18886992F, -0.44823506F, 0.0F, 0.84600437F, -0.3725218F, 0.38145F, 0.0F, 0.89787275F, -0.17802091F, -0.40265754F, 0.0F, 0.21780656F, -0.9698323F, -0.10947895F, 0.0F, -0.15180314F, -0.7788918F, -0.6085091F, 0.0F, -0.2600385F, -0.4755398F, -0.840382F, 0.0F, 0.5723135F, -0.7474341F, -0.33734185F, 0.0F, -0.7174141F, 0.16990171F, -0.67561114F, 0.0F, -0.6841808F, 0.021457076F, -0.72899675F, 0.0F, -0.2007448F, 0.06555606F, -0.9774477F, 0.0F, -0.11488037F, -0.8044887F, 0.5827524F, 0.0F, -0.787035F, 0.03447489F, 0.6159443F, 0.0F, -0.20155965F, 0.68598723F, 0.69913894F, 0.0F, -0.085810825F, -0.10920836F, -0.99030805F, 0.0F, 0.5532693F, 0.73252505F, -0.39661077F, 0.0F, -0.18424894F, -0.9777375F, -0.100407675F, 0.0F, 0.07754738F, -0.9111506F, 0.40471104F, 0.0F, 0.13998385F, 0.7601631F, -0.63447344F, 0.0F, 0.44844192F, -0.84528923F, 0.29049253F, 0.0F};
   private static final int PrimeX = 501125321;
   private static final int PrimeY = 1136930381;
   private static final int PrimeZ = 1720413743;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$RotationType3D;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularDistanceFunction;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularReturnType;

   public FastNoiseLite() {
      this.mNoiseType = FastNoiseLite.NoiseType.OpenSimplex2;
      this.mRotationType3D = FastNoiseLite.RotationType3D.None;
      this.mTransformType3D = FastNoiseLite.TransformType3D.DefaultOpenSimplex2;
      this.mFractalType = FastNoiseLite.FractalType.None;
      this.mOctaves = 3;
      this.mLacunarity = 2.0F;
      this.mGain = 0.5F;
      this.mWeightedStrength = 0.0F;
      this.mPingPongStrength = 2.0F;
      this.mFractalBounding = 0.5714286F;
      this.mCellularDistanceFunction = FastNoiseLite.CellularDistanceFunction.EuclideanSq;
      this.mCellularReturnType = FastNoiseLite.CellularReturnType.Distance;
      this.mCellularJitterModifier = 1.0F;
      this.mDomainWarpType = FastNoiseLite.DomainWarpType.OpenSimplex2;
      this.mWarpTransformType3D = FastNoiseLite.TransformType3D.DefaultOpenSimplex2;
      this.mDomainWarpAmp = 1.0F;
   }

   public FastNoiseLite(int seed) {
      this.mNoiseType = FastNoiseLite.NoiseType.OpenSimplex2;
      this.mRotationType3D = FastNoiseLite.RotationType3D.None;
      this.mTransformType3D = FastNoiseLite.TransformType3D.DefaultOpenSimplex2;
      this.mFractalType = FastNoiseLite.FractalType.None;
      this.mOctaves = 3;
      this.mLacunarity = 2.0F;
      this.mGain = 0.5F;
      this.mWeightedStrength = 0.0F;
      this.mPingPongStrength = 2.0F;
      this.mFractalBounding = 0.5714286F;
      this.mCellularDistanceFunction = FastNoiseLite.CellularDistanceFunction.EuclideanSq;
      this.mCellularReturnType = FastNoiseLite.CellularReturnType.Distance;
      this.mCellularJitterModifier = 1.0F;
      this.mDomainWarpType = FastNoiseLite.DomainWarpType.OpenSimplex2;
      this.mWarpTransformType3D = FastNoiseLite.TransformType3D.DefaultOpenSimplex2;
      this.mDomainWarpAmp = 1.0F;
      this.SetSeed(seed);
   }

   public void SetSeed(int seed) {
      this.mSeed = seed;
   }

   public void SetFrequency(float frequency) {
      this.mFrequency = frequency;
   }

   public void SetNoiseType(FastNoiseLite.NoiseType noiseType) {
      this.mNoiseType = noiseType;
      this.UpdateTransformType3D();
   }

   public void SetRotationType3D(FastNoiseLite.RotationType3D rotationType3D) {
      this.mRotationType3D = rotationType3D;
      this.UpdateTransformType3D();
      this.UpdateWarpTransformType3D();
   }

   public void SetFractalType(FastNoiseLite.FractalType fractalType) {
      this.mFractalType = fractalType;
   }

   public void SetFractalOctaves(int octaves) {
      this.mOctaves = octaves;
      this.CalculateFractalBounding();
   }

   public void SetFractalLacunarity(float lacunarity) {
      this.mLacunarity = lacunarity;
   }

   public void SetFractalGain(float gain) {
      this.mGain = gain;
      this.CalculateFractalBounding();
   }

   public void SetFractalWeightedStrength(float weightedStrength) {
      this.mWeightedStrength = weightedStrength;
   }

   public void SetFractalPingPongStrength(float pingPongStrength) {
      this.mPingPongStrength = pingPongStrength;
   }

   public void SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction cellularDistanceFunction) {
      this.mCellularDistanceFunction = cellularDistanceFunction;
   }

   public void SetCellularReturnType(FastNoiseLite.CellularReturnType cellularReturnType) {
      this.mCellularReturnType = cellularReturnType;
   }

   public void SetCellularJitter(float cellularJitter) {
      this.mCellularJitterModifier = cellularJitter;
   }

   public void SetDomainWarpType(FastNoiseLite.DomainWarpType domainWarpType) {
      this.mDomainWarpType = domainWarpType;
      this.UpdateWarpTransformType3D();
   }

   public void SetDomainWarpAmp(float domainWarpAmp) {
      this.mDomainWarpAmp = domainWarpAmp;
   }

   public float GetNoise(float x, float y) {
      x *= this.mFrequency;
      y *= this.mFrequency;
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType()[this.mNoiseType.ordinal()]) {
      case 1:
      case 2:
         float SQRT3 = 1.7320508F;
         float F2 = 0.3660254F;
         float t = (x + y) * 0.3660254F;
         x += t;
         y += t;
      default:
         switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType()[this.mFractalType.ordinal()]) {
         case 2:
            return this.GenFractalFBm(x, y);
         case 3:
            return this.GenFractalRidged(x, y);
         case 4:
            return this.GenFractalPingPong(x, y);
         default:
            return this.GenNoiseSingle(this.mSeed, x, y);
         }
      }
   }

   public float GetNoise(float x, float y, float z) {
      x *= this.mFrequency;
      y *= this.mFrequency;
      z *= this.mFrequency;
      float R3;
      float r;
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D()[this.mTransformType3D.ordinal()]) {
      case 2:
         R3 = x + y;
         r = R3 * -0.21132487F;
         z *= 0.57735026F;
         x += r - z;
         y = y + r - z;
         z += R3 * 0.57735026F;
         break;
      case 3:
         R3 = x + z;
         r = R3 * -0.21132487F;
         y *= 0.57735026F;
         x += r - y;
         z += r - y;
         y += R3 * 0.57735026F;
         break;
      case 4:
         R3 = 0.6666667F;
         r = (x + y + z) * 0.6666667F;
         x = r - x;
         y = r - y;
         z = r - z;
      }

      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType()[this.mFractalType.ordinal()]) {
      case 2:
         return this.GenFractalFBm(x, y, z);
      case 3:
         return this.GenFractalRidged(x, y, z);
      case 4:
         return this.GenFractalPingPong(x, y, z);
      default:
         return this.GenNoiseSingle(this.mSeed, x, y, z);
      }
   }

   public void DomainWarp(FastNoiseLite.Vector2 coord) {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType()[this.mFractalType.ordinal()]) {
      case 5:
         this.DomainWarpFractalProgressive(coord);
         break;
      case 6:
         this.DomainWarpFractalIndependent(coord);
         break;
      default:
         this.DomainWarpSingle(coord);
      }

   }

   public void DomainWarp(FastNoiseLite.Vector3 coord) {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType()[this.mFractalType.ordinal()]) {
      case 5:
         this.DomainWarpFractalProgressive(coord);
         break;
      case 6:
         this.DomainWarpFractalIndependent(coord);
         break;
      default:
         this.DomainWarpSingle(coord);
      }

   }

   private static float FastMin(float a, float b) {
      return a < b ? a : b;
   }

   private static float FastMax(float a, float b) {
      return a > b ? a : b;
   }

   private static float FastAbs(float f) {
      return f < 0.0F ? -f : f;
   }

   private static float FastSqrt(float f) {
      return (float)Math.sqrt((double)f);
   }

   private static int FastFloor(float f) {
      return f >= 0.0F ? (int)f : (int)f - 1;
   }

   private static int FastRound(float f) {
      return f >= 0.0F ? (int)(f + 0.5F) : (int)(f - 0.5F);
   }

   private static float Lerp(float a, float b, float t) {
      return a + t * (b - a);
   }

   private static float InterpHermite(float t) {
      return t * t * (3.0F - 2.0F * t);
   }

   private static float InterpQuintic(float t) {
      return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
   }

   private static float CubicLerp(float a, float b, float c, float d, float t) {
      float p = d - c - (a - b);
      return t * t * t * p + t * t * (a - b - p) + t * (c - a) + b;
   }

   private static float PingPong(float t) {
      t -= (float)((int)(t * 0.5F) * 2);
      return t < 1.0F ? t : 2.0F - t;
   }

   private void CalculateFractalBounding() {
      float gain = FastAbs(this.mGain);
      float amp = gain;
      float ampFractal = 1.0F;

      for(int i = 1; i < this.mOctaves; ++i) {
         ampFractal += amp;
         amp *= gain;
      }

      this.mFractalBounding = 1.0F / ampFractal;
   }

   private static int Hash(int seed, int xPrimed, int yPrimed) {
      int hash = seed ^ xPrimed ^ yPrimed;
      hash *= 668265261;
      return hash;
   }

   private static int Hash(int seed, int xPrimed, int yPrimed, int zPrimed) {
      int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed;
      hash *= 668265261;
      return hash;
   }

   private static float ValCoord(int seed, int xPrimed, int yPrimed) {
      int hash = Hash(seed, xPrimed, yPrimed);
      hash *= hash;
      hash ^= hash << 19;
      return (float)hash * 4.656613E-10F;
   }

   private static float ValCoord(int seed, int xPrimed, int yPrimed, int zPrimed) {
      int hash = Hash(seed, xPrimed, yPrimed, zPrimed);
      hash *= hash;
      hash ^= hash << 19;
      return (float)hash * 4.656613E-10F;
   }

   private static float GradCoord(int seed, int xPrimed, int yPrimed, float xd, float yd) {
      int hash = Hash(seed, xPrimed, yPrimed);
      hash ^= hash >> 15;
      hash &= 254;
      float xg = Gradients2D[hash];
      float yg = Gradients2D[hash | 1];
      return xd * xg + yd * yg;
   }

   private static float GradCoord(int seed, int xPrimed, int yPrimed, int zPrimed, float xd, float yd, float zd) {
      int hash = Hash(seed, xPrimed, yPrimed, zPrimed);
      hash ^= hash >> 15;
      hash &= 252;
      float xg = Gradients3D[hash];
      float yg = Gradients3D[hash | 1];
      float zg = Gradients3D[hash | 2];
      return xd * xg + yd * yg + zd * zg;
   }

   private float GenNoiseSingle(int seed, float x, float y) {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType()[this.mNoiseType.ordinal()]) {
      case 1:
         return this.SingleSimplex(seed, x, y);
      case 2:
         return this.SingleOpenSimplex2S(seed, x, y);
      case 3:
         return this.SingleCellular(seed, x, y);
      case 4:
         return this.SinglePerlin(seed, x, y);
      case 5:
         return this.SingleValueCubic(seed, x, y);
      case 6:
         return this.SingleValue(seed, x, y);
      default:
         return 0.0F;
      }
   }

   private float GenNoiseSingle(int seed, float x, float y, float z) {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType()[this.mNoiseType.ordinal()]) {
      case 1:
         return this.SingleOpenSimplex2(seed, x, y, z);
      case 2:
         return this.SingleOpenSimplex2S(seed, x, y, z);
      case 3:
         return this.SingleCellular(seed, x, y, z);
      case 4:
         return this.SinglePerlin(seed, x, y, z);
      case 5:
         return this.SingleValueCubic(seed, x, y, z);
      case 6:
         return this.SingleValue(seed, x, y, z);
      default:
         return 0.0F;
      }
   }

   private void UpdateTransformType3D() {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$RotationType3D()[this.mRotationType3D.ordinal()]) {
      case 2:
         this.mTransformType3D = FastNoiseLite.TransformType3D.ImproveXYPlanes;
         break;
      case 3:
         this.mTransformType3D = FastNoiseLite.TransformType3D.ImproveXZPlanes;
         break;
      default:
         switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType()[this.mNoiseType.ordinal()]) {
         case 1:
         case 2:
            this.mTransformType3D = FastNoiseLite.TransformType3D.DefaultOpenSimplex2;
            break;
         default:
            this.mTransformType3D = FastNoiseLite.TransformType3D.None;
         }
      }

   }

   private void UpdateWarpTransformType3D() {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$RotationType3D()[this.mRotationType3D.ordinal()]) {
      case 2:
         this.mWarpTransformType3D = FastNoiseLite.TransformType3D.ImproveXYPlanes;
         break;
      case 3:
         this.mWarpTransformType3D = FastNoiseLite.TransformType3D.ImproveXZPlanes;
         break;
      default:
         switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType()[this.mDomainWarpType.ordinal()]) {
         case 1:
         case 2:
            this.mWarpTransformType3D = FastNoiseLite.TransformType3D.DefaultOpenSimplex2;
            break;
         default:
            this.mWarpTransformType3D = FastNoiseLite.TransformType3D.None;
         }
      }

   }

   private float GenFractalFBm(float x, float y) {
      int seed = this.mSeed;
      float sum = 0.0F;
      float amp = this.mFractalBounding;

      for(int i = 0; i < this.mOctaves; ++i) {
         float noise = this.GenNoiseSingle(seed++, x, y);
         sum += noise * amp;
         amp *= Lerp(1.0F, FastMin(noise + 1.0F, 2.0F) * 0.5F, this.mWeightedStrength);
         x *= this.mLacunarity;
         y *= this.mLacunarity;
         amp *= this.mGain;
      }

      return sum;
   }

   private float GenFractalFBm(float x, float y, float z) {
      int seed = this.mSeed;
      float sum = 0.0F;
      float amp = this.mFractalBounding;

      for(int i = 0; i < this.mOctaves; ++i) {
         float noise = this.GenNoiseSingle(seed++, x, y, z);
         sum += noise * amp;
         amp *= Lerp(1.0F, (noise + 1.0F) * 0.5F, this.mWeightedStrength);
         x *= this.mLacunarity;
         y *= this.mLacunarity;
         z *= this.mLacunarity;
         amp *= this.mGain;
      }

      return sum;
   }

   private float GenFractalRidged(float x, float y) {
      int seed = this.mSeed;
      float sum = 0.0F;
      float amp = this.mFractalBounding;

      for(int i = 0; i < this.mOctaves; ++i) {
         float noise = FastAbs(this.GenNoiseSingle(seed++, x, y));
         sum += (noise * -2.0F + 1.0F) * amp;
         amp *= Lerp(1.0F, 1.0F - noise, this.mWeightedStrength);
         x *= this.mLacunarity;
         y *= this.mLacunarity;
         amp *= this.mGain;
      }

      return sum;
   }

   private float GenFractalRidged(float x, float y, float z) {
      int seed = this.mSeed;
      float sum = 0.0F;
      float amp = this.mFractalBounding;

      for(int i = 0; i < this.mOctaves; ++i) {
         float noise = FastAbs(this.GenNoiseSingle(seed++, x, y, z));
         sum += (noise * -2.0F + 1.0F) * amp;
         amp *= Lerp(1.0F, 1.0F - noise, this.mWeightedStrength);
         x *= this.mLacunarity;
         y *= this.mLacunarity;
         z *= this.mLacunarity;
         amp *= this.mGain;
      }

      return sum;
   }

   private float GenFractalPingPong(float x, float y) {
      int seed = this.mSeed;
      float sum = 0.0F;
      float amp = this.mFractalBounding;

      for(int i = 0; i < this.mOctaves; ++i) {
         float noise = PingPong((this.GenNoiseSingle(seed++, x, y) + 1.0F) * this.mPingPongStrength);
         sum += (noise - 0.5F) * 2.0F * amp;
         amp *= Lerp(1.0F, noise, this.mWeightedStrength);
         x *= this.mLacunarity;
         y *= this.mLacunarity;
         amp *= this.mGain;
      }

      return sum;
   }

   private float GenFractalPingPong(float x, float y, float z) {
      int seed = this.mSeed;
      float sum = 0.0F;
      float amp = this.mFractalBounding;

      for(int i = 0; i < this.mOctaves; ++i) {
         float noise = PingPong((this.GenNoiseSingle(seed++, x, y, z) + 1.0F) * this.mPingPongStrength);
         sum += (noise - 0.5F) * 2.0F * amp;
         amp *= Lerp(1.0F, noise, this.mWeightedStrength);
         x *= this.mLacunarity;
         y *= this.mLacunarity;
         z *= this.mLacunarity;
         amp *= this.mGain;
      }

      return sum;
   }

   private float SingleSimplex(int seed, float x, float y) {
      float SQRT3 = 1.7320508F;
      float G2 = 0.21132487F;
      int i = FastFloor(x);
      int j = FastFloor(y);
      float xi = x - (float)i;
      float yi = y - (float)j;
      float t = (xi + yi) * 0.21132487F;
      float x0 = xi - t;
      float y0 = yi - t;
      i *= 501125321;
      j *= 1136930381;
      float a = 0.5F - x0 * x0 - y0 * y0;
      float n0;
      if (a <= 0.0F) {
         n0 = 0.0F;
      } else {
         n0 = a * a * a * a * GradCoord(seed, i, j, x0, y0);
      }

      float c = 3.1547005F * t + -0.6666666F + a;
      float n2;
      float x1;
      float y1;
      if (c <= 0.0F) {
         n2 = 0.0F;
      } else {
         x1 = x0 + -0.57735026F;
         y1 = y0 + -0.57735026F;
         n2 = c * c * c * c * GradCoord(seed, i + 501125321, j + 1136930381, x1, y1);
      }

      float n1;
      float b;
      if (y0 > x0) {
         x1 = x0 + 0.21132487F;
         y1 = y0 + -0.7886751F;
         b = 0.5F - x1 * x1 - y1 * y1;
         if (b <= 0.0F) {
            n1 = 0.0F;
         } else {
            n1 = b * b * b * b * GradCoord(seed, i, j + 1136930381, x1, y1);
         }
      } else {
         x1 = x0 + -0.7886751F;
         y1 = y0 + 0.21132487F;
         b = 0.5F - x1 * x1 - y1 * y1;
         if (b <= 0.0F) {
            n1 = 0.0F;
         } else {
            n1 = b * b * b * b * GradCoord(seed, i + 501125321, j, x1, y1);
         }
      }

      return (n0 + n1 + n2) * 99.83685F;
   }

   private float SingleOpenSimplex2(int seed, float x, float y, float z) {
      int i = FastRound(x);
      int j = FastRound(y);
      int k = FastRound(z);
      float x0 = x - (float)i;
      float y0 = y - (float)j;
      float z0 = z - (float)k;
      int xNSign = (int)(-1.0F - x0) | 1;
      int yNSign = (int)(-1.0F - y0) | 1;
      int zNSign = (int)(-1.0F - z0) | 1;
      float ax0 = (float)xNSign * -x0;
      float ay0 = (float)yNSign * -y0;
      float az0 = (float)zNSign * -z0;
      i *= 501125321;
      j *= 1136930381;
      k *= 1720413743;
      float value = 0.0F;
      float a = 0.6F - x0 * x0 - (y0 * y0 + z0 * z0);
      int l = 0;

      while(true) {
         if (a > 0.0F) {
            value += a * a * a * a * GradCoord(seed, i, j, k, x0, y0, z0);
         }

         float b;
         if (ax0 >= ay0 && ax0 >= az0) {
            b = a + ax0 + ax0;
            if (b > 1.0F) {
               --b;
               value += b * b * b * b * GradCoord(seed, i - xNSign * 501125321, j, k, x0 + (float)xNSign, y0, z0);
            }
         } else if (ay0 > ax0 && ay0 >= az0) {
            b = a + ay0 + ay0;
            if (b > 1.0F) {
               --b;
               value += b * b * b * b * GradCoord(seed, i, j - yNSign * 1136930381, k, x0, y0 + (float)yNSign, z0);
            }
         } else {
            b = a + az0 + az0;
            if (b > 1.0F) {
               --b;
               value += b * b * b * b * GradCoord(seed, i, j, k - zNSign * 1720413743, x0, y0, z0 + (float)zNSign);
            }
         }

         if (l == 1) {
            return value * 32.694283F;
         }

         ax0 = 0.5F - ax0;
         ay0 = 0.5F - ay0;
         az0 = 0.5F - az0;
         x0 = (float)xNSign * ax0;
         y0 = (float)yNSign * ay0;
         z0 = (float)zNSign * az0;
         a += 0.75F - ax0 - (ay0 + az0);
         i += xNSign >> 1 & 501125321;
         j += yNSign >> 1 & 1136930381;
         k += zNSign >> 1 & 1720413743;
         xNSign = -xNSign;
         yNSign = -yNSign;
         zNSign = -zNSign;
         seed = ~seed;
         ++l;
      }
   }

   private float SingleOpenSimplex2S(int seed, float x, float y) {
      float SQRT3 = 1.7320508F;
      float G2 = 0.21132487F;
      int i = FastFloor(x);
      int j = FastFloor(y);
      float xi = x - (float)i;
      float yi = y - (float)j;
      i *= 501125321;
      j *= 1136930381;
      int i1 = i + 501125321;
      int j1 = j + 1136930381;
      float t = (xi + yi) * 0.21132487F;
      float x0 = xi - t;
      float y0 = yi - t;
      float a0 = 0.6666667F - x0 * x0 - y0 * y0;
      float value = a0 * a0 * a0 * a0 * GradCoord(seed, i, j, x0, y0);
      float a1 = 3.1547005F * t + -0.6666666F + a0;
      float x1 = x0 - 0.57735026F;
      float y1 = y0 - 0.57735026F;
      value += a1 * a1 * a1 * a1 * GradCoord(seed, i1, j1, x1, y1);
      float xmyi = xi - yi;
      float x3;
      float y3;
      float a3;
      if (t > 0.21132487F) {
         if (xi + xmyi > 1.0F) {
            x3 = x0 + -1.3660254F;
            y3 = y0 + -0.3660254F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i + 1002250642, j + 1136930381, x3, y3);
            }
         } else {
            x3 = x0 + 0.21132487F;
            y3 = y0 + -0.7886751F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i, j + 1136930381, x3, y3);
            }
         }

         if (yi - xmyi > 1.0F) {
            x3 = x0 + -0.3660254F;
            y3 = y0 + -1.3660254F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i + 501125321, j + -2021106534, x3, y3);
            }
         } else {
            x3 = x0 + -0.7886751F;
            y3 = y0 + 0.21132487F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i + 501125321, j, x3, y3);
            }
         }
      } else {
         if (xi + xmyi < 0.0F) {
            x3 = x0 + 0.7886751F;
            y3 = y0 - 0.21132487F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i - 501125321, j, x3, y3);
            }
         } else {
            x3 = x0 + -0.7886751F;
            y3 = y0 + 0.21132487F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i + 501125321, j, x3, y3);
            }
         }

         if (yi < xmyi) {
            x3 = x0 - 0.21132487F;
            y3 = y0 - -0.7886751F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i, j - 1136930381, x3, y3);
            }
         } else {
            x3 = x0 + 0.21132487F;
            y3 = y0 + -0.7886751F;
            a3 = 0.6666667F - x3 * x3 - y3 * y3;
            if (a3 > 0.0F) {
               value += a3 * a3 * a3 * a3 * GradCoord(seed, i, j + 1136930381, x3, y3);
            }
         }
      }

      return value * 18.241962F;
   }

   private float SingleOpenSimplex2S(int seed, float x, float y, float z) {
      int i = FastFloor(x);
      int j = FastFloor(y);
      int k = FastFloor(z);
      float xi = x - (float)i;
      float yi = y - (float)j;
      float zi = z - (float)k;
      i *= 501125321;
      j *= 1136930381;
      k *= 1720413743;
      int seed2 = seed + 1293373;
      int xNMask = (int)(-0.5F - xi);
      int yNMask = (int)(-0.5F - yi);
      int zNMask = (int)(-0.5F - zi);
      float x0 = xi + (float)xNMask;
      float y0 = yi + (float)yNMask;
      float z0 = zi + (float)zNMask;
      float a0 = 0.75F - x0 * x0 - y0 * y0 - z0 * z0;
      float value = a0 * a0 * a0 * a0 * GradCoord(seed, i + (xNMask & 501125321), j + (yNMask & 1136930381), k + (zNMask & 1720413743), x0, y0, z0);
      float x1 = xi - 0.5F;
      float y1 = yi - 0.5F;
      float z1 = zi - 0.5F;
      float a1 = 0.75F - x1 * x1 - y1 * y1 - z1 * z1;
      value += a1 * a1 * a1 * a1 * GradCoord(seed2, i + 501125321, j + 1136930381, k + 1720413743, x1, y1, z1);
      float xAFlipMask0 = (float)((xNMask | 1) << 1) * x1;
      float yAFlipMask0 = (float)((yNMask | 1) << 1) * y1;
      float zAFlipMask0 = (float)((zNMask | 1) << 1) * z1;
      float xAFlipMask1 = (float)(-2 - (xNMask << 2)) * x1 - 1.0F;
      float yAFlipMask1 = (float)(-2 - (yNMask << 2)) * y1 - 1.0F;
      float zAFlipMask1 = (float)(-2 - (zNMask << 2)) * z1 - 1.0F;
      boolean skip5 = false;
      float a2 = xAFlipMask0 + a0;
      float a3;
      float a4;
      float a7;
      float a8;
      if (a2 > 0.0F) {
         a3 = x0 - (float)(xNMask | 1);
         value += a2 * a2 * a2 * a2 * GradCoord(seed, i + (~xNMask & 501125321), j + (yNMask & 1136930381), k + (zNMask & 1720413743), a3, y0, z0);
      } else {
         a3 = yAFlipMask0 + zAFlipMask0 + a0;
         if (a3 > 0.0F) {
            a7 = y0 - (float)(yNMask | 1);
            a8 = z0 - (float)(zNMask | 1);
            value += a3 * a3 * a3 * a3 * GradCoord(seed, i + (xNMask & 501125321), j + (~yNMask & 1136930381), k + (~zNMask & 1720413743), x0, a7, a8);
         }

         a4 = xAFlipMask1 + a1;
         if (a4 > 0.0F) {
            a7 = (float)(xNMask | 1) + x1;
            value += a4 * a4 * a4 * a4 * GradCoord(seed2, i + (xNMask & 1002250642), j + 1136930381, k + 1720413743, a7, y1, z1);
            skip5 = true;
         }
      }

      boolean skip9 = false;
      a4 = yAFlipMask0 + a0;
      float xD;
      if (a4 > 0.0F) {
         a8 = y0 - (float)(yNMask | 1);
         value += a4 * a4 * a4 * a4 * GradCoord(seed, i + (xNMask & 501125321), j + (~yNMask & 1136930381), k + (zNMask & 1720413743), x0, a8, z0);
      } else {
         a7 = xAFlipMask0 + zAFlipMask0 + a0;
         if (a7 > 0.0F) {
            a8 = x0 - (float)(xNMask | 1);
            xD = z0 - (float)(zNMask | 1);
            value += a7 * a7 * a7 * a7 * GradCoord(seed, i + (~xNMask & 501125321), j + (yNMask & 1136930381), k + (~zNMask & 1720413743), a8, y0, xD);
         }

         a8 = yAFlipMask1 + a1;
         if (a8 > 0.0F) {
            xD = (float)(yNMask | 1) + y1;
            value += a8 * a8 * a8 * a8 * GradCoord(seed2, i + 501125321, j + (yNMask & -2021106534), k + 1720413743, x1, xD, z1);
            skip9 = true;
         }
      }

      boolean skipD = false;
      a8 = zAFlipMask0 + a0;
      float aD;
      float yD;
      if (a8 > 0.0F) {
         yD = z0 - (float)(zNMask | 1);
         value += a8 * a8 * a8 * a8 * GradCoord(seed, i + (xNMask & 501125321), j + (yNMask & 1136930381), k + (~zNMask & 1720413743), x0, y0, yD);
      } else {
         aD = xAFlipMask0 + yAFlipMask0 + a0;
         if (aD > 0.0F) {
            xD = x0 - (float)(xNMask | 1);
            yD = y0 - (float)(yNMask | 1);
            value += aD * aD * aD * aD * GradCoord(seed, i + (~xNMask & 501125321), j + (~yNMask & 1136930381), k + (zNMask & 1720413743), xD, yD, z0);
         }

         xD = zAFlipMask1 + a1;
         if (xD > 0.0F) {
            float zC = (float)(zNMask | 1) + z1;
            value += xD * xD * xD * xD * GradCoord(seed2, i + 501125321, j + 1136930381, k + (zNMask & -854139810), x1, y1, zC);
            skipD = true;
         }
      }

      float z9;
      if (!skip5) {
         aD = yAFlipMask1 + zAFlipMask1 + a1;
         if (aD > 0.0F) {
            yD = (float)(yNMask | 1) + y1;
            z9 = (float)(zNMask | 1) + z1;
            value += aD * aD * aD * aD * GradCoord(seed2, i + 501125321, j + (yNMask & -2021106534), k + (zNMask & -854139810), x1, yD, z9);
         }
      }

      if (!skip9) {
         aD = xAFlipMask1 + zAFlipMask1 + a1;
         if (aD > 0.0F) {
            xD = (float)(xNMask | 1) + x1;
            z9 = (float)(zNMask | 1) + z1;
            value += aD * aD * aD * aD * GradCoord(seed2, i + (xNMask & 1002250642), j + 1136930381, k + (zNMask & -854139810), xD, y1, z9);
         }
      }

      if (!skipD) {
         aD = xAFlipMask1 + yAFlipMask1 + a1;
         if (aD > 0.0F) {
            xD = (float)(xNMask | 1) + x1;
            yD = (float)(yNMask | 1) + y1;
            value += aD * aD * aD * aD * GradCoord(seed2, i + (xNMask & 1002250642), j + (yNMask & -2021106534), k + 1720413743, xD, yD, z1);
         }
      }

      return value * 9.046026F;
   }

   private float SingleCellular(int seed, float x, float y) {
      float distance0;
      float distance1;
      int closestHash;
      int xr = FastRound(x);
      int yr = FastRound(y);
      distance0 = Float.MAX_VALUE;
      distance1 = Float.MAX_VALUE;
      closestHash = 0;
      float cellularJitter = 0.43701595F * this.mCellularJitterModifier;
      int xPrimed = (xr - 1) * 501125321;
      int yPrimedBase = (yr - 1) * 1136930381;
      int xi;
      int yPrimed;
      int yi;
      int hash;
      int idx;
      float vecX;
      float vecY;
      float newDistance;
      label88:
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularDistanceFunction()[this.mCellularDistanceFunction.ordinal()]) {
      case 1:
      case 2:
      default:
         xi = xr - 1;

         while(true) {
            if (xi > xr + 1) {
               break label88;
            }

            yPrimed = yPrimedBase;

            for(yi = yr - 1; yi <= yr + 1; ++yi) {
               hash = Hash(seed, xPrimed, yPrimed);
               idx = hash & 510;
               vecX = (float)xi - x + RandVecs2D[idx] * cellularJitter;
               vecY = (float)yi - y + RandVecs2D[idx | 1] * cellularJitter;
               newDistance = vecX * vecX + vecY * vecY;
               distance1 = FastMax(FastMin(distance1, newDistance), distance0);
               if (newDistance < distance0) {
                  distance0 = newDistance;
                  closestHash = hash;
               }

               yPrimed += 1136930381;
            }

            xPrimed += 501125321;
            ++xi;
         }
      case 3:
         xi = xr - 1;

         while(true) {
            if (xi > xr + 1) {
               break label88;
            }

            yPrimed = yPrimedBase;

            for(yi = yr - 1; yi <= yr + 1; ++yi) {
               hash = Hash(seed, xPrimed, yPrimed);
               idx = hash & 510;
               vecX = (float)xi - x + RandVecs2D[idx] * cellularJitter;
               vecY = (float)yi - y + RandVecs2D[idx | 1] * cellularJitter;
               newDistance = FastAbs(vecX) + FastAbs(vecY);
               distance1 = FastMax(FastMin(distance1, newDistance), distance0);
               if (newDistance < distance0) {
                  distance0 = newDistance;
                  closestHash = hash;
               }

               yPrimed += 1136930381;
            }

            xPrimed += 501125321;
            ++xi;
         }
      case 4:
         for(xi = xr - 1; xi <= xr + 1; ++xi) {
            yPrimed = yPrimedBase;

            for(yi = yr - 1; yi <= yr + 1; ++yi) {
               hash = Hash(seed, xPrimed, yPrimed);
               idx = hash & 510;
               vecX = (float)xi - x + RandVecs2D[idx] * cellularJitter;
               vecY = (float)yi - y + RandVecs2D[idx | 1] * cellularJitter;
               newDistance = FastAbs(vecX) + FastAbs(vecY) + vecX * vecX + vecY * vecY;
               distance1 = FastMax(FastMin(distance1, newDistance), distance0);
               if (newDistance < distance0) {
                  distance0 = newDistance;
                  closestHash = hash;
               }

               yPrimed += 1136930381;
            }

            xPrimed += 501125321;
         }
      }

      if (this.mCellularDistanceFunction == FastNoiseLite.CellularDistanceFunction.Euclidean && this.mCellularReturnType != FastNoiseLite.CellularReturnType.CellValue) {
         distance0 = FastSqrt(distance0);
         if (this.mCellularReturnType != FastNoiseLite.CellularReturnType.Distance) {
            distance1 = FastSqrt(distance1);
         }
      }

      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularReturnType()[this.mCellularReturnType.ordinal()]) {
      case 1:
         return (float)closestHash * 4.656613E-10F;
      case 2:
         return distance0 - 1.0F;
      case 3:
         return distance1 - 1.0F;
      case 4:
         return (distance1 + distance0) * 0.5F - 1.0F;
      case 5:
         return distance1 - distance0 - 1.0F;
      case 6:
         return distance1 * distance0 * 0.5F - 1.0F;
      case 7:
         return distance0 / distance1 - 1.0F;
      default:
         return 0.0F;
      }
   }

   private float SingleCellular(int seed, float x, float y, float z) {
      float distance0;
      float distance1;
      int closestHash;
      int xr = FastRound(x);
      int yr = FastRound(y);
      int zr = FastRound(z);
      distance0 = Float.MAX_VALUE;
      distance1 = Float.MAX_VALUE;
      closestHash = 0;
      float cellularJitter = 0.39614353F * this.mCellularJitterModifier;
      int xPrimed = (xr - 1) * 501125321;
      int yPrimedBase = (yr - 1) * 1136930381;
      int zPrimedBase = (zr - 1) * 1720413743;
      int xi;
      int yPrimed;
      int yi;
      int zPrimed;
      int zi;
      int hash;
      int idx;
      float vecX;
      float vecY;
      float vecZ;
      float newDistance;
      label116:
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularDistanceFunction()[this.mCellularDistanceFunction.ordinal()]) {
      case 1:
      case 2:
         xi = xr - 1;

         while(true) {
            if (xi > xr + 1) {
               break label116;
            }

            yPrimed = yPrimedBase;

            for(yi = yr - 1; yi <= yr + 1; ++yi) {
               zPrimed = zPrimedBase;

               for(zi = zr - 1; zi <= zr + 1; ++zi) {
                  hash = Hash(seed, xPrimed, yPrimed, zPrimed);
                  idx = hash & 1020;
                  vecX = (float)xi - x + RandVecs3D[idx] * cellularJitter;
                  vecY = (float)yi - y + RandVecs3D[idx | 1] * cellularJitter;
                  vecZ = (float)zi - z + RandVecs3D[idx | 2] * cellularJitter;
                  newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;
                  distance1 = FastMax(FastMin(distance1, newDistance), distance0);
                  if (newDistance < distance0) {
                     distance0 = newDistance;
                     closestHash = hash;
                  }

                  zPrimed += 1720413743;
               }

               yPrimed += 1136930381;
            }

            xPrimed += 501125321;
            ++xi;
         }
      case 3:
         xi = xr - 1;

         while(true) {
            if (xi > xr + 1) {
               break label116;
            }

            yPrimed = yPrimedBase;

            for(yi = yr - 1; yi <= yr + 1; ++yi) {
               zPrimed = zPrimedBase;

               for(zi = zr - 1; zi <= zr + 1; ++zi) {
                  hash = Hash(seed, xPrimed, yPrimed, zPrimed);
                  idx = hash & 1020;
                  vecX = (float)xi - x + RandVecs3D[idx] * cellularJitter;
                  vecY = (float)yi - y + RandVecs3D[idx | 1] * cellularJitter;
                  vecZ = (float)zi - z + RandVecs3D[idx | 2] * cellularJitter;
                  newDistance = FastAbs(vecX) + FastAbs(vecY) + FastAbs(vecZ);
                  distance1 = FastMax(FastMin(distance1, newDistance), distance0);
                  if (newDistance < distance0) {
                     distance0 = newDistance;
                     closestHash = hash;
                  }

                  zPrimed += 1720413743;
               }

               yPrimed += 1136930381;
            }

            xPrimed += 501125321;
            ++xi;
         }
      case 4:
         for(xi = xr - 1; xi <= xr + 1; ++xi) {
            yPrimed = yPrimedBase;

            for(yi = yr - 1; yi <= yr + 1; ++yi) {
               zPrimed = zPrimedBase;

               for(zi = zr - 1; zi <= zr + 1; ++zi) {
                  hash = Hash(seed, xPrimed, yPrimed, zPrimed);
                  idx = hash & 1020;
                  vecX = (float)xi - x + RandVecs3D[idx] * cellularJitter;
                  vecY = (float)yi - y + RandVecs3D[idx | 1] * cellularJitter;
                  vecZ = (float)zi - z + RandVecs3D[idx | 2] * cellularJitter;
                  newDistance = FastAbs(vecX) + FastAbs(vecY) + FastAbs(vecZ) + vecX * vecX + vecY * vecY + vecZ * vecZ;
                  distance1 = FastMax(FastMin(distance1, newDistance), distance0);
                  if (newDistance < distance0) {
                     distance0 = newDistance;
                     closestHash = hash;
                  }

                  zPrimed += 1720413743;
               }

               yPrimed += 1136930381;
            }

            xPrimed += 501125321;
         }
      }

      if (this.mCellularDistanceFunction == FastNoiseLite.CellularDistanceFunction.Euclidean && this.mCellularReturnType != FastNoiseLite.CellularReturnType.CellValue) {
         distance0 = FastSqrt(distance0);
         if (this.mCellularReturnType != FastNoiseLite.CellularReturnType.Distance) {
            distance1 = FastSqrt(distance1);
         }
      }

      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularReturnType()[this.mCellularReturnType.ordinal()]) {
      case 1:
         return (float)closestHash * 4.656613E-10F;
      case 2:
         return distance0 - 1.0F;
      case 3:
         return distance1 - 1.0F;
      case 4:
         return (distance1 + distance0) * 0.5F - 1.0F;
      case 5:
         return distance1 - distance0 - 1.0F;
      case 6:
         return distance1 * distance0 * 0.5F - 1.0F;
      case 7:
         return distance0 / distance1 - 1.0F;
      default:
         return 0.0F;
      }
   }

   private float SinglePerlin(int seed, float x, float y) {
      int x0 = FastFloor(x);
      int y0 = FastFloor(y);
      float xd0 = x - (float)x0;
      float yd0 = y - (float)y0;
      float xd1 = xd0 - 1.0F;
      float yd1 = yd0 - 1.0F;
      float xs = InterpQuintic(xd0);
      float ys = InterpQuintic(yd0);
      x0 *= 501125321;
      y0 *= 1136930381;
      int x1 = x0 + 501125321;
      int y1 = y0 + 1136930381;
      float xf0 = Lerp(GradCoord(seed, x0, y0, xd0, yd0), GradCoord(seed, x1, y0, xd1, yd0), xs);
      float xf1 = Lerp(GradCoord(seed, x0, y1, xd0, yd1), GradCoord(seed, x1, y1, xd1, yd1), xs);
      return Lerp(xf0, xf1, ys) * 1.4247692F;
   }

   private float SinglePerlin(int seed, float x, float y, float z) {
      int x0 = FastFloor(x);
      int y0 = FastFloor(y);
      int z0 = FastFloor(z);
      float xd0 = x - (float)x0;
      float yd0 = y - (float)y0;
      float zd0 = z - (float)z0;
      float xd1 = xd0 - 1.0F;
      float yd1 = yd0 - 1.0F;
      float zd1 = zd0 - 1.0F;
      float xs = InterpQuintic(xd0);
      float ys = InterpQuintic(yd0);
      float zs = InterpQuintic(zd0);
      x0 *= 501125321;
      y0 *= 1136930381;
      z0 *= 1720413743;
      int x1 = x0 + 501125321;
      int y1 = y0 + 1136930381;
      int z1 = z0 + 1720413743;
      float xf00 = Lerp(GradCoord(seed, x0, y0, z0, xd0, yd0, zd0), GradCoord(seed, x1, y0, z0, xd1, yd0, zd0), xs);
      float xf10 = Lerp(GradCoord(seed, x0, y1, z0, xd0, yd1, zd0), GradCoord(seed, x1, y1, z0, xd1, yd1, zd0), xs);
      float xf01 = Lerp(GradCoord(seed, x0, y0, z1, xd0, yd0, zd1), GradCoord(seed, x1, y0, z1, xd1, yd0, zd1), xs);
      float xf11 = Lerp(GradCoord(seed, x0, y1, z1, xd0, yd1, zd1), GradCoord(seed, x1, y1, z1, xd1, yd1, zd1), xs);
      float yf0 = Lerp(xf00, xf10, ys);
      float yf1 = Lerp(xf01, xf11, ys);
      return Lerp(yf0, yf1, zs) * 0.9649214F;
   }

   private float SingleValueCubic(int seed, float x, float y) {
      int x1 = FastFloor(x);
      int y1 = FastFloor(y);
      float xs = x - (float)x1;
      float ys = y - (float)y1;
      x1 *= 501125321;
      y1 *= 1136930381;
      int x0 = x1 - 501125321;
      int y0 = y1 - 1136930381;
      int x2 = x1 + 501125321;
      int y2 = y1 + 1136930381;
      int x3 = x1 + 1002250642;
      int y3 = y1 + -2021106534;
      return CubicLerp(CubicLerp(ValCoord(seed, x0, y0), ValCoord(seed, x1, y0), ValCoord(seed, x2, y0), ValCoord(seed, x3, y0), xs), CubicLerp(ValCoord(seed, x0, y1), ValCoord(seed, x1, y1), ValCoord(seed, x2, y1), ValCoord(seed, x3, y1), xs), CubicLerp(ValCoord(seed, x0, y2), ValCoord(seed, x1, y2), ValCoord(seed, x2, y2), ValCoord(seed, x3, y2), xs), CubicLerp(ValCoord(seed, x0, y3), ValCoord(seed, x1, y3), ValCoord(seed, x2, y3), ValCoord(seed, x3, y3), xs), ys) * 0.44444445F;
   }

   private float SingleValueCubic(int seed, float x, float y, float z) {
      int x1 = FastFloor(x);
      int y1 = FastFloor(y);
      int z1 = FastFloor(z);
      float xs = x - (float)x1;
      float ys = y - (float)y1;
      float zs = z - (float)z1;
      x1 *= 501125321;
      y1 *= 1136930381;
      z1 *= 1720413743;
      int x0 = x1 - 501125321;
      int y0 = y1 - 1136930381;
      int z0 = z1 - 1720413743;
      int x2 = x1 + 501125321;
      int y2 = y1 + 1136930381;
      int z2 = z1 + 1720413743;
      int x3 = x1 + 1002250642;
      int y3 = y1 + -2021106534;
      int z3 = z1 + -854139810;
      return CubicLerp(CubicLerp(CubicLerp(ValCoord(seed, x0, y0, z0), ValCoord(seed, x1, y0, z0), ValCoord(seed, x2, y0, z0), ValCoord(seed, x3, y0, z0), xs), CubicLerp(ValCoord(seed, x0, y1, z0), ValCoord(seed, x1, y1, z0), ValCoord(seed, x2, y1, z0), ValCoord(seed, x3, y1, z0), xs), CubicLerp(ValCoord(seed, x0, y2, z0), ValCoord(seed, x1, y2, z0), ValCoord(seed, x2, y2, z0), ValCoord(seed, x3, y2, z0), xs), CubicLerp(ValCoord(seed, x0, y3, z0), ValCoord(seed, x1, y3, z0), ValCoord(seed, x2, y3, z0), ValCoord(seed, x3, y3, z0), xs), ys), CubicLerp(CubicLerp(ValCoord(seed, x0, y0, z1), ValCoord(seed, x1, y0, z1), ValCoord(seed, x2, y0, z1), ValCoord(seed, x3, y0, z1), xs), CubicLerp(ValCoord(seed, x0, y1, z1), ValCoord(seed, x1, y1, z1), ValCoord(seed, x2, y1, z1), ValCoord(seed, x3, y1, z1), xs), CubicLerp(ValCoord(seed, x0, y2, z1), ValCoord(seed, x1, y2, z1), ValCoord(seed, x2, y2, z1), ValCoord(seed, x3, y2, z1), xs), CubicLerp(ValCoord(seed, x0, y3, z1), ValCoord(seed, x1, y3, z1), ValCoord(seed, x2, y3, z1), ValCoord(seed, x3, y3, z1), xs), ys), CubicLerp(CubicLerp(ValCoord(seed, x0, y0, z2), ValCoord(seed, x1, y0, z2), ValCoord(seed, x2, y0, z2), ValCoord(seed, x3, y0, z2), xs), CubicLerp(ValCoord(seed, x0, y1, z2), ValCoord(seed, x1, y1, z2), ValCoord(seed, x2, y1, z2), ValCoord(seed, x3, y1, z2), xs), CubicLerp(ValCoord(seed, x0, y2, z2), ValCoord(seed, x1, y2, z2), ValCoord(seed, x2, y2, z2), ValCoord(seed, x3, y2, z2), xs), CubicLerp(ValCoord(seed, x0, y3, z2), ValCoord(seed, x1, y3, z2), ValCoord(seed, x2, y3, z2), ValCoord(seed, x3, y3, z2), xs), ys), CubicLerp(CubicLerp(ValCoord(seed, x0, y0, z3), ValCoord(seed, x1, y0, z3), ValCoord(seed, x2, y0, z3), ValCoord(seed, x3, y0, z3), xs), CubicLerp(ValCoord(seed, x0, y1, z3), ValCoord(seed, x1, y1, z3), ValCoord(seed, x2, y1, z3), ValCoord(seed, x3, y1, z3), xs), CubicLerp(ValCoord(seed, x0, y2, z3), ValCoord(seed, x1, y2, z3), ValCoord(seed, x2, y2, z3), ValCoord(seed, x3, y2, z3), xs), CubicLerp(ValCoord(seed, x0, y3, z3), ValCoord(seed, x1, y3, z3), ValCoord(seed, x2, y3, z3), ValCoord(seed, x3, y3, z3), xs), ys), zs) * 0.2962963F;
   }

   private float SingleValue(int seed, float x, float y) {
      int x0 = FastFloor(x);
      int y0 = FastFloor(y);
      float xs = InterpHermite(x - (float)x0);
      float ys = InterpHermite(y - (float)y0);
      x0 *= 501125321;
      y0 *= 1136930381;
      int x1 = x0 + 501125321;
      int y1 = y0 + 1136930381;
      float xf0 = Lerp(ValCoord(seed, x0, y0), ValCoord(seed, x1, y0), xs);
      float xf1 = Lerp(ValCoord(seed, x0, y1), ValCoord(seed, x1, y1), xs);
      return Lerp(xf0, xf1, ys);
   }

   private float SingleValue(int seed, float x, float y, float z) {
      int x0 = FastFloor(x);
      int y0 = FastFloor(y);
      int z0 = FastFloor(z);
      float xs = InterpHermite(x - (float)x0);
      float ys = InterpHermite(y - (float)y0);
      float zs = InterpHermite(z - (float)z0);
      x0 *= 501125321;
      y0 *= 1136930381;
      z0 *= 1720413743;
      int x1 = x0 + 501125321;
      int y1 = y0 + 1136930381;
      int z1 = z0 + 1720413743;
      float xf00 = Lerp(ValCoord(seed, x0, y0, z0), ValCoord(seed, x1, y0, z0), xs);
      float xf10 = Lerp(ValCoord(seed, x0, y1, z0), ValCoord(seed, x1, y1, z0), xs);
      float xf01 = Lerp(ValCoord(seed, x0, y0, z1), ValCoord(seed, x1, y0, z1), xs);
      float xf11 = Lerp(ValCoord(seed, x0, y1, z1), ValCoord(seed, x1, y1, z1), xs);
      float yf0 = Lerp(xf00, xf10, ys);
      float yf1 = Lerp(xf01, xf11, ys);
      return Lerp(yf0, yf1, zs);
   }

   private void DoSingleDomainWarp(int seed, float amp, float freq, float x, float y, FastNoiseLite.Vector2 coord) {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType()[this.mDomainWarpType.ordinal()]) {
      case 1:
         this.SingleDomainWarpSimplexGradient(seed, amp * 38.283688F, freq, x, y, coord, false);
         break;
      case 2:
         this.SingleDomainWarpSimplexGradient(seed, amp * 16.0F, freq, x, y, coord, true);
         break;
      case 3:
         this.SingleDomainWarpBasicGrid(seed, amp, freq, x, y, coord);
      }

   }

   private void DoSingleDomainWarp(int seed, float amp, float freq, float x, float y, float z, FastNoiseLite.Vector3 coord) {
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType()[this.mDomainWarpType.ordinal()]) {
      case 1:
         this.SingleDomainWarpOpenSimplex2Gradient(seed, amp * 32.694283F, freq, x, y, z, coord, false);
         break;
      case 2:
         this.SingleDomainWarpOpenSimplex2Gradient(seed, amp * 7.716049F, freq, x, y, z, coord, true);
         break;
      case 3:
         this.SingleDomainWarpBasicGrid(seed, amp, freq, x, y, z, coord);
      }

   }

   private void DomainWarpSingle(FastNoiseLite.Vector2 coord) {
      int seed = this.mSeed;
      float amp = this.mDomainWarpAmp * this.mFractalBounding;
      float freq = this.mFrequency;
      float xs = coord.x;
      float ys = coord.y;
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType()[this.mDomainWarpType.ordinal()]) {
      case 1:
      case 2:
         float SQRT3 = 1.7320508F;
         float F2 = 0.3660254F;
         float t = (xs + ys) * 0.3660254F;
         xs += t;
         ys += t;
      default:
         this.DoSingleDomainWarp(seed, amp, freq, xs, ys, coord);
      }
   }

   private void DomainWarpSingle(FastNoiseLite.Vector3 coord) {
      int seed = this.mSeed;
      float amp = this.mDomainWarpAmp * this.mFractalBounding;
      float freq = this.mFrequency;
      float xs = coord.x;
      float ys = coord.y;
      float zs = coord.z;
      float R3;
      float r;
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D()[this.mWarpTransformType3D.ordinal()]) {
      case 2:
         R3 = xs + ys;
         r = R3 * -0.21132487F;
         zs *= 0.57735026F;
         xs += r - zs;
         ys = ys + r - zs;
         zs += R3 * 0.57735026F;
         break;
      case 3:
         R3 = xs + zs;
         r = R3 * -0.21132487F;
         ys *= 0.57735026F;
         xs += r - ys;
         zs += r - ys;
         ys += R3 * 0.57735026F;
         break;
      case 4:
         R3 = 0.6666667F;
         r = (xs + ys + zs) * 0.6666667F;
         xs = r - xs;
         ys = r - ys;
         zs = r - zs;
      }

      this.DoSingleDomainWarp(seed, amp, freq, xs, ys, zs, coord);
   }

   private void DomainWarpFractalProgressive(FastNoiseLite.Vector2 coord) {
      int seed = this.mSeed;
      float amp = this.mDomainWarpAmp * this.mFractalBounding;
      float freq = this.mFrequency;
      int i = 0;

      while(i < this.mOctaves) {
         float xs = coord.x;
         float ys = coord.y;
         switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType()[this.mDomainWarpType.ordinal()]) {
         case 1:
         case 2:
            float SQRT3 = 1.7320508F;
            float F2 = 0.3660254F;
            float t = (xs + ys) * 0.3660254F;
            xs += t;
            ys += t;
         default:
            this.DoSingleDomainWarp(seed, amp, freq, xs, ys, coord);
            ++seed;
            amp *= this.mGain;
            freq *= this.mLacunarity;
            ++i;
         }
      }

   }

   private void DomainWarpFractalProgressive(FastNoiseLite.Vector3 coord) {
      int seed = this.mSeed;
      float amp = this.mDomainWarpAmp * this.mFractalBounding;
      float freq = this.mFrequency;

      for(int i = 0; i < this.mOctaves; ++i) {
         float xs = coord.x;
         float ys = coord.y;
         float zs = coord.z;
         float R3;
         float r;
         switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D()[this.mWarpTransformType3D.ordinal()]) {
         case 2:
            R3 = xs + ys;
            r = R3 * -0.21132487F;
            zs *= 0.57735026F;
            xs += r - zs;
            ys = ys + r - zs;
            zs += R3 * 0.57735026F;
            break;
         case 3:
            R3 = xs + zs;
            r = R3 * -0.21132487F;
            ys *= 0.57735026F;
            xs += r - ys;
            zs += r - ys;
            ys += R3 * 0.57735026F;
            break;
         case 4:
            R3 = 0.6666667F;
            r = (xs + ys + zs) * 0.6666667F;
            xs = r - xs;
            ys = r - ys;
            zs = r - zs;
         }

         this.DoSingleDomainWarp(seed, amp, freq, xs, ys, zs, coord);
         ++seed;
         amp *= this.mGain;
         freq *= this.mLacunarity;
      }

   }

   private void DomainWarpFractalIndependent(FastNoiseLite.Vector2 coord) {
      float xs = coord.x;
      float ys = coord.y;
      float amp;
      float freq;
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType()[this.mDomainWarpType.ordinal()]) {
      case 1:
      case 2:
         float SQRT3 = 1.7320508F;
         amp = 0.3660254F;
         freq = (xs + ys) * 0.3660254F;
         xs += freq;
         ys += freq;
      default:
         int seed = this.mSeed;
         amp = this.mDomainWarpAmp * this.mFractalBounding;
         freq = this.mFrequency;

         for(int i = 0; i < this.mOctaves; ++i) {
            this.DoSingleDomainWarp(seed, amp, freq, xs, ys, coord);
            ++seed;
            amp *= this.mGain;
            freq *= this.mLacunarity;
         }

      }
   }

   private void DomainWarpFractalIndependent(FastNoiseLite.Vector3 coord) {
      float xs = coord.x;
      float ys = coord.y;
      float zs = coord.z;
      float R3;
      float amp;
      switch($SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D()[this.mWarpTransformType3D.ordinal()]) {
      case 2:
         R3 = xs + ys;
         amp = R3 * -0.21132487F;
         zs *= 0.57735026F;
         xs += amp - zs;
         ys = ys + amp - zs;
         zs += R3 * 0.57735026F;
         break;
      case 3:
         R3 = xs + zs;
         amp = R3 * -0.21132487F;
         ys *= 0.57735026F;
         xs += amp - ys;
         zs += amp - ys;
         ys += R3 * 0.57735026F;
         break;
      case 4:
         R3 = 0.6666667F;
         amp = (xs + ys + zs) * 0.6666667F;
         xs = amp - xs;
         ys = amp - ys;
         zs = amp - zs;
      }

      int seed = this.mSeed;
      amp = this.mDomainWarpAmp * this.mFractalBounding;
      float freq = this.mFrequency;

      for(int i = 0; i < this.mOctaves; ++i) {
         this.DoSingleDomainWarp(seed, amp, freq, xs, ys, zs, coord);
         ++seed;
         amp *= this.mGain;
         freq *= this.mLacunarity;
      }

   }

   private void SingleDomainWarpBasicGrid(int seed, float warpAmp, float frequency, float x, float y, FastNoiseLite.Vector2 coord) {
      float xf = x * frequency;
      float yf = y * frequency;
      int x0 = FastFloor(xf);
      int y0 = FastFloor(yf);
      float xs = InterpHermite(xf - (float)x0);
      float ys = InterpHermite(yf - (float)y0);
      x0 *= 501125321;
      y0 *= 1136930381;
      int x1 = x0 + 501125321;
      int y1 = y0 + 1136930381;
      int hash0 = Hash(seed, x0, y0) & 510;
      int hash1 = Hash(seed, x1, y0) & 510;
      float lx0x = Lerp(RandVecs2D[hash0], RandVecs2D[hash1], xs);
      float ly0x = Lerp(RandVecs2D[hash0 | 1], RandVecs2D[hash1 | 1], xs);
      hash0 = Hash(seed, x0, y1) & 510;
      hash1 = Hash(seed, x1, y1) & 510;
      float lx1x = Lerp(RandVecs2D[hash0], RandVecs2D[hash1], xs);
      float ly1x = Lerp(RandVecs2D[hash0 | 1], RandVecs2D[hash1 | 1], xs);
      coord.x += Lerp(lx0x, lx1x, ys) * warpAmp;
      coord.y += Lerp(ly0x, ly1x, ys) * warpAmp;
   }

   private void SingleDomainWarpBasicGrid(int seed, float warpAmp, float frequency, float x, float y, float z, FastNoiseLite.Vector3 coord) {
      float xf = x * frequency;
      float yf = y * frequency;
      float zf = z * frequency;
      int x0 = FastFloor(xf);
      int y0 = FastFloor(yf);
      int z0 = FastFloor(zf);
      float xs = InterpHermite(xf - (float)x0);
      float ys = InterpHermite(yf - (float)y0);
      float zs = InterpHermite(zf - (float)z0);
      x0 *= 501125321;
      y0 *= 1136930381;
      z0 *= 1720413743;
      int x1 = x0 + 501125321;
      int y1 = y0 + 1136930381;
      int z1 = z0 + 1720413743;
      int hash0 = Hash(seed, x0, y0, z0) & 1020;
      int hash1 = Hash(seed, x1, y0, z0) & 1020;
      float lx0x = Lerp(RandVecs3D[hash0], RandVecs3D[hash1], xs);
      float ly0x = Lerp(RandVecs3D[hash0 | 1], RandVecs3D[hash1 | 1], xs);
      float lz0x = Lerp(RandVecs3D[hash0 | 2], RandVecs3D[hash1 | 2], xs);
      hash0 = Hash(seed, x0, y1, z0) & 1020;
      hash1 = Hash(seed, x1, y1, z0) & 1020;
      float lx1x = Lerp(RandVecs3D[hash0], RandVecs3D[hash1], xs);
      float ly1x = Lerp(RandVecs3D[hash0 | 1], RandVecs3D[hash1 | 1], xs);
      float lz1x = Lerp(RandVecs3D[hash0 | 2], RandVecs3D[hash1 | 2], xs);
      float lx0y = Lerp(lx0x, lx1x, ys);
      float ly0y = Lerp(ly0x, ly1x, ys);
      float lz0y = Lerp(lz0x, lz1x, ys);
      hash0 = Hash(seed, x0, y0, z1) & 1020;
      hash1 = Hash(seed, x1, y0, z1) & 1020;
      lx0x = Lerp(RandVecs3D[hash0], RandVecs3D[hash1], xs);
      ly0x = Lerp(RandVecs3D[hash0 | 1], RandVecs3D[hash1 | 1], xs);
      lz0x = Lerp(RandVecs3D[hash0 | 2], RandVecs3D[hash1 | 2], xs);
      hash0 = Hash(seed, x0, y1, z1) & 1020;
      hash1 = Hash(seed, x1, y1, z1) & 1020;
      lx1x = Lerp(RandVecs3D[hash0], RandVecs3D[hash1], xs);
      ly1x = Lerp(RandVecs3D[hash0 | 1], RandVecs3D[hash1 | 1], xs);
      lz1x = Lerp(RandVecs3D[hash0 | 2], RandVecs3D[hash1 | 2], xs);
      coord.x += Lerp(lx0y, Lerp(lx0x, lx1x, ys), zs) * warpAmp;
      coord.y += Lerp(ly0y, Lerp(ly0x, ly1x, ys), zs) * warpAmp;
      coord.z += Lerp(lz0y, Lerp(lz0x, lz1x, ys), zs) * warpAmp;
   }

   private void SingleDomainWarpSimplexGradient(int seed, float warpAmp, float frequency, float x, float y, FastNoiseLite.Vector2 coord, boolean outGradOnly) {
      float SQRT3 = 1.7320508F;
      float G2 = 0.21132487F;
      x *= frequency;
      y *= frequency;
      int i = FastFloor(x);
      int j = FastFloor(y);
      float xi = x - (float)i;
      float yi = y - (float)j;
      float t = (xi + yi) * 0.21132487F;
      float x0 = xi - t;
      float y0 = yi - t;
      i *= 501125321;
      j *= 1136930381;
      float vy = 0.0F;
      float vx = 0.0F;
      float a = 0.5F - x0 * x0 - y0 * y0;
      float aaaa;
      float x1;
      float y1;
      float yo;
      float xg;
      float xg;
      if (a > 0.0F) {
         aaaa = a * a * a * a;
         int hash;
         if (outGradOnly) {
            hash = Hash(seed, i, j) & 510;
            x1 = RandVecs2D[hash];
            y1 = RandVecs2D[hash | 1];
         } else {
            hash = Hash(seed, i, j);
            int index1 = hash & 254;
            int index2 = hash >> 7 & 510;
            yo = Gradients2D[index1];
            float yg = Gradients2D[index1 | 1];
            float value = x0 * yo + y0 * yg;
            xg = RandVecs2D[index2];
            xg = RandVecs2D[index2 | 1];
            x1 = value * xg;
            y1 = value * xg;
         }

         vx += aaaa * x1;
         vy += aaaa * y1;
      }

      aaaa = 3.1547005F * t + -0.6666666F + a;
      float yg;
      float value;
      float xgo;
      float b;
      float bbbb;
      float xo;
      int hash;
      int index1;
      if (aaaa > 0.0F) {
         x1 = x0 + -0.57735026F;
         y1 = y0 + -0.57735026F;
         b = aaaa * aaaa * aaaa * aaaa;
         int hash;
         if (outGradOnly) {
            hash = Hash(seed, i + 501125321, j + 1136930381) & 510;
            bbbb = RandVecs2D[hash];
            xo = RandVecs2D[hash | 1];
         } else {
            hash = Hash(seed, i + 501125321, j + 1136930381);
            hash = hash & 254;
            index1 = hash >> 7 & 510;
            xg = Gradients2D[hash];
            xg = Gradients2D[hash | 1];
            yg = x1 * xg + y1 * xg;
            value = RandVecs2D[index1];
            xgo = RandVecs2D[index1 | 1];
            bbbb = yg * value;
            xo = yg * xgo;
         }

         vx += b * bbbb;
         vy += b * xo;
      }

      float ygo;
      int index2;
      if (y0 > x0) {
         x1 = x0 + 0.21132487F;
         y1 = y0 + -0.7886751F;
         b = 0.5F - x1 * x1 - y1 * y1;
         if (b > 0.0F) {
            bbbb = b * b * b * b;
            if (outGradOnly) {
               hash = Hash(seed, i, j + 1136930381) & 510;
               xo = RandVecs2D[hash];
               yo = RandVecs2D[hash | 1];
            } else {
               hash = Hash(seed, i, j + 1136930381);
               index1 = hash & 254;
               index2 = hash >> 7 & 510;
               xg = Gradients2D[index1];
               yg = Gradients2D[index1 | 1];
               value = x1 * xg + y1 * yg;
               xgo = RandVecs2D[index2];
               ygo = RandVecs2D[index2 | 1];
               xo = value * xgo;
               yo = value * ygo;
            }

            vx += bbbb * xo;
            vy += bbbb * yo;
         }
      } else {
         x1 = x0 + -0.7886751F;
         y1 = y0 + 0.21132487F;
         b = 0.5F - x1 * x1 - y1 * y1;
         if (b > 0.0F) {
            bbbb = b * b * b * b;
            if (outGradOnly) {
               hash = Hash(seed, i + 501125321, j) & 510;
               xo = RandVecs2D[hash];
               yo = RandVecs2D[hash | 1];
            } else {
               hash = Hash(seed, i + 501125321, j);
               index1 = hash & 254;
               index2 = hash >> 7 & 510;
               xg = Gradients2D[index1];
               yg = Gradients2D[index1 | 1];
               value = x1 * xg + y1 * yg;
               xgo = RandVecs2D[index2];
               ygo = RandVecs2D[index2 | 1];
               xo = value * xgo;
               yo = value * ygo;
            }

            vx += bbbb * xo;
            vy += bbbb * yo;
         }
      }

      coord.x += vx * warpAmp;
      coord.y += vy * warpAmp;
   }

   private void SingleDomainWarpOpenSimplex2Gradient(int seed, float warpAmp, float frequency, float x, float y, float z, FastNoiseLite.Vector3 coord, boolean outGradOnly) {
      x *= frequency;
      y *= frequency;
      z *= frequency;
      int i = FastRound(x);
      int j = FastRound(y);
      int k = FastRound(z);
      float x0 = x - (float)i;
      float y0 = y - (float)j;
      float z0 = z - (float)k;
      int xNSign = (int)(-x0 - 1.0F) | 1;
      int yNSign = (int)(-y0 - 1.0F) | 1;
      int zNSign = (int)(-z0 - 1.0F) | 1;
      float ax0 = (float)xNSign * -x0;
      float ay0 = (float)yNSign * -y0;
      float az0 = (float)zNSign * -z0;
      i *= 501125321;
      j *= 1136930381;
      k *= 1720413743;
      float vz = 0.0F;
      float vy = 0.0F;
      float vx = 0.0F;
      float a = 0.6F - x0 * x0 - (y0 * y0 + z0 * z0);
      int l = 0;

      while(true) {
         float b;
         float bbbb;
         float xo;
         float yo;
         float zo;
         if (a > 0.0F) {
            b = a * a * a * a;
            float xo;
            float yo;
            float zo;
            int hash;
            if (outGradOnly) {
               hash = Hash(seed, i, j, k) & 1020;
               xo = RandVecs3D[hash];
               yo = RandVecs3D[hash | 1];
               zo = RandVecs3D[hash | 2];
            } else {
               hash = Hash(seed, i, j, k);
               int index1 = hash & 252;
               int index2 = hash >> 6 & 1020;
               bbbb = Gradients3D[index1];
               xo = Gradients3D[index1 | 1];
               yo = Gradients3D[index1 | 2];
               zo = x0 * bbbb + y0 * xo + z0 * yo;
               float xgo = RandVecs3D[index2];
               float ygo = RandVecs3D[index2 | 1];
               float zgo = RandVecs3D[index2 | 2];
               xo = zo * xgo;
               yo = zo * ygo;
               zo = zo * zgo;
            }

            vx += b * xo;
            vy += b * yo;
            vz += b * zo;
         }

         int i1 = i;
         int j1 = j;
         int k1 = k;
         float x1 = x0;
         float y1 = y0;
         float z1 = z0;
         if (ax0 >= ay0 && ax0 >= az0) {
            x1 = x0 + (float)xNSign;
            b = a + ax0 + ax0;
            i1 = i - xNSign * 501125321;
         } else if (ay0 > ax0 && ay0 >= az0) {
            y1 = y0 + (float)yNSign;
            b = a + ay0 + ay0;
            j1 = j - yNSign * 1136930381;
         } else {
            z1 = z0 + (float)zNSign;
            b = a + az0 + az0;
            k1 = k - zNSign * 1720413743;
         }

         if (b > 1.0F) {
            --b;
            bbbb = b * b * b * b;
            int hash;
            if (outGradOnly) {
               hash = Hash(seed, i1, j1, k1) & 1020;
               xo = RandVecs3D[hash];
               yo = RandVecs3D[hash | 1];
               zo = RandVecs3D[hash | 2];
            } else {
               hash = Hash(seed, i1, j1, k1);
               int index1 = hash & 252;
               int index2 = hash >> 6 & 1020;
               float xg = Gradients3D[index1];
               float yg = Gradients3D[index1 | 1];
               float zg = Gradients3D[index1 | 2];
               float value = x1 * xg + y1 * yg + z1 * zg;
               float xgo = RandVecs3D[index2];
               float ygo = RandVecs3D[index2 | 1];
               float zgo = RandVecs3D[index2 | 2];
               xo = value * xgo;
               yo = value * ygo;
               zo = value * zgo;
            }

            vx += bbbb * xo;
            vy += bbbb * yo;
            vz += bbbb * zo;
         }

         if (l == 1) {
            coord.x += vx * warpAmp;
            coord.y += vy * warpAmp;
            coord.z += vz * warpAmp;
            return;
         }

         ax0 = 0.5F - ax0;
         ay0 = 0.5F - ay0;
         az0 = 0.5F - az0;
         x0 = (float)xNSign * ax0;
         y0 = (float)yNSign * ay0;
         z0 = (float)zNSign * az0;
         a += 0.75F - ax0 - (ay0 + az0);
         i += xNSign >> 1 & 501125321;
         j += yNSign >> 1 & 1136930381;
         k += zNSign >> 1 & 1720413743;
         xNSign = -xNSign;
         yNSign = -yNSign;
         zNSign = -zNSign;
         seed += 1293373;
         ++l;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.NoiseType.values().length];

         try {
            var0[FastNoiseLite.NoiseType.Cellular.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[FastNoiseLite.NoiseType.OpenSimplex2.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[FastNoiseLite.NoiseType.OpenSimplex2S.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[FastNoiseLite.NoiseType.Perlin.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.NoiseType.Value.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.NoiseType.ValueCubic.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$NoiseType = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.FractalType.values().length];

         try {
            var0[FastNoiseLite.FractalType.DomainWarpIndependent.ordinal()] = 6;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[FastNoiseLite.FractalType.DomainWarpProgressive.ordinal()] = 5;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[FastNoiseLite.FractalType.FBm.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[FastNoiseLite.FractalType.None.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.FractalType.PingPong.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.FractalType.Ridged.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$FractalType = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.TransformType3D.values().length];

         try {
            var0[FastNoiseLite.TransformType3D.DefaultOpenSimplex2.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[FastNoiseLite.TransformType3D.ImproveXYPlanes.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.TransformType3D.ImproveXZPlanes.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.TransformType3D.None.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$TransformType3D = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$RotationType3D() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$RotationType3D;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.RotationType3D.values().length];

         try {
            var0[FastNoiseLite.RotationType3D.ImproveXYPlanes.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.RotationType3D.ImproveXZPlanes.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.RotationType3D.None.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$RotationType3D = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.DomainWarpType.values().length];

         try {
            var0[FastNoiseLite.DomainWarpType.BasicGrid.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.DomainWarpType.OpenSimplex2.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.DomainWarpType.OpenSimplex2Reduced.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$DomainWarpType = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularDistanceFunction() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularDistanceFunction;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.CellularDistanceFunction.values().length];

         try {
            var0[FastNoiseLite.CellularDistanceFunction.Euclidean.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[FastNoiseLite.CellularDistanceFunction.EuclideanSq.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.CellularDistanceFunction.Hybrid.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.CellularDistanceFunction.Manhattan.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularDistanceFunction = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularReturnType() {
      int[] var10000 = $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularReturnType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FastNoiseLite.CellularReturnType.values().length];

         try {
            var0[FastNoiseLite.CellularReturnType.CellValue.ordinal()] = 1;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[FastNoiseLite.CellularReturnType.Distance.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[FastNoiseLite.CellularReturnType.Distance2.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[FastNoiseLite.CellularReturnType.Distance2Add.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[FastNoiseLite.CellularReturnType.Distance2Div.ordinal()] = 7;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FastNoiseLite.CellularReturnType.Distance2Mul.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FastNoiseLite.CellularReturnType.Distance2Sub.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$xyz$cucumber$base$utils$math$FastNoiseLite$CellularReturnType = var0;
         return var0;
      }
   }

   public static enum CellularDistanceFunction {
      Euclidean,
      EuclideanSq,
      Manhattan,
      Hybrid;
   }

   public static enum CellularReturnType {
      CellValue,
      Distance,
      Distance2,
      Distance2Add,
      Distance2Sub,
      Distance2Mul,
      Distance2Div;
   }

   public static enum DomainWarpType {
      OpenSimplex2,
      OpenSimplex2Reduced,
      BasicGrid;
   }

   public static enum FractalType {
      None,
      FBm,
      Ridged,
      PingPong,
      DomainWarpProgressive,
      DomainWarpIndependent;
   }

   public static enum NoiseType {
      OpenSimplex2,
      OpenSimplex2S,
      Cellular,
      Perlin,
      ValueCubic,
      Value;
   }

   public static enum RotationType3D {
      None,
      ImproveXYPlanes,
      ImproveXZPlanes;
   }

   private static enum TransformType3D {
      None,
      ImproveXYPlanes,
      ImproveXZPlanes,
      DefaultOpenSimplex2;
   }

   public static class Vector2 {
      public float x;
      public float y;

      public Vector2(float x, float y) {
         this.x = x;
         this.y = y;
      }
   }

   public static class Vector3 {
      public float x;
      public float y;
      public float z;

      public Vector3(float x, float y, float z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }
}
